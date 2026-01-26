/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.objimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ObjParser {
    private ObjParser() {
    }

    @Nonnull
    public static ObjMesh parse(@Nonnull Path path) throws IOException, ObjParseException {
        ArrayList<float[]> vertices = new ArrayList<float[]>();
        ArrayList<float[]> uvCoordinates = new ArrayList<float[]>();
        ArrayList<int[]> faces = new ArrayList<int[]>();
        ArrayList<int[]> faceUvIndices = new ArrayList<int[]>();
        ArrayList<String> faceMaterials = new ArrayList<String>();
        String mtlLib = null;
        String currentMaterial = null;
        try (BufferedReader reader = Files.newBufferedReader(path);){
            String line;
            int lineNum = 0;
            block19: while ((line = reader.readLine()) != null) {
                String[] parts;
                ++lineNum;
                if ((line = line.trim()).isEmpty() || line.startsWith("#") || (parts = line.split("\\s+")).length == 0) continue;
                switch (parts[0]) {
                    case "v": {
                        ObjParser.parseVertex(parts, vertices, lineNum);
                        break;
                    }
                    case "vt": {
                        ObjParser.parseUvCoordinate(parts, uvCoordinates, lineNum);
                        break;
                    }
                    case "f": {
                        int faceCountBefore = faces.size();
                        ObjParser.parseFace(parts, faces, faceUvIndices, uvCoordinates.size(), lineNum);
                        int facesAdded = faces.size() - faceCountBefore;
                        for (int i = 0; i < facesAdded; ++i) {
                            faceMaterials.add(currentMaterial);
                        }
                        continue block19;
                    }
                    case "mtllib": {
                        if (parts.length <= 1) break;
                        mtlLib = parts[1].trim();
                        break;
                    }
                    case "usemtl": {
                        if (parts.length <= 1) break;
                        currentMaterial = parts[1].trim();
                    }
                }
            }
        }
        if (vertices.isEmpty()) {
            throw new ObjParseException("OBJ file contains no vertices");
        }
        if (faces.isEmpty()) {
            throw new ObjParseException("OBJ file contains no faces");
        }
        return new ObjMesh(vertices, uvCoordinates, faces, faceUvIndices, faceMaterials, mtlLib);
    }

    private static void parseVertex(String[] parts, List<float[]> vertices, int lineNum) throws ObjParseException {
        if (parts.length < 4) {
            throw new ObjParseException("Invalid vertex at line " + lineNum + ": expected at least 3 coordinates");
        }
        try {
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            float z = Float.parseFloat(parts[3]);
            vertices.add(new float[]{x, y, z});
        }
        catch (NumberFormatException e) {
            throw new ObjParseException("Invalid vertex coordinates at line " + lineNum);
        }
    }

    private static void parseUvCoordinate(String[] parts, List<float[]> uvCoordinates, int lineNum) throws ObjParseException {
        if (parts.length < 3) {
            throw new ObjParseException("Invalid UV coordinate at line " + lineNum + ": expected at least 2 values");
        }
        try {
            float u = Float.parseFloat(parts[1]);
            float v = Float.parseFloat(parts[2]);
            uvCoordinates.add(new float[]{u, v});
        }
        catch (NumberFormatException e) {
            throw new ObjParseException("Invalid UV coordinates at line " + lineNum);
        }
    }

    private static void parseFace(String[] parts, List<int[]> faces, List<int[]> faceUvIndices, int uvCount, int lineNum) throws ObjParseException {
        int i;
        if (parts.length < 4) {
            throw new ObjParseException("Invalid face at line " + lineNum + ": expected at least 3 vertices");
        }
        int[] vertexIndices = new int[parts.length - 1];
        int[] uvIndices = new int[parts.length - 1];
        boolean hasUvs = false;
        for (i = 1; i < parts.length; ++i) {
            String vertexData = parts[i];
            String[] components = vertexData.split("/");
            try {
                int vIndex = Integer.parseInt(components[0]);
                vertexIndices[i - 1] = vIndex > 0 ? vIndex - 1 : vIndex;
            }
            catch (NumberFormatException e) {
                throw new ObjParseException("Invalid face vertex index at line " + lineNum);
            }
            if (components.length >= 2 && !components[1].isEmpty()) {
                try {
                    int uvIndex = Integer.parseInt(components[1]);
                    uvIndices[i - 1] = uvIndex > 0 ? uvIndex - 1 : uvIndex + uvCount;
                    hasUvs = true;
                }
                catch (NumberFormatException e) {
                    uvIndices[i - 1] = -1;
                }
                continue;
            }
            uvIndices[i - 1] = -1;
        }
        if (vertexIndices.length == 3) {
            faces.add(vertexIndices);
            faceUvIndices.add((int[])(hasUvs ? uvIndices : null));
        } else if (vertexIndices.length == 4) {
            faces.add(new int[]{vertexIndices[0], vertexIndices[1], vertexIndices[2]});
            faces.add(new int[]{vertexIndices[0], vertexIndices[2], vertexIndices[3]});
            if (hasUvs) {
                faceUvIndices.add(new int[]{uvIndices[0], uvIndices[1], uvIndices[2]});
                faceUvIndices.add(new int[]{uvIndices[0], uvIndices[2], uvIndices[3]});
            } else {
                faceUvIndices.add(null);
                faceUvIndices.add(null);
            }
        } else {
            for (i = 1; i < vertexIndices.length - 1; ++i) {
                faces.add(new int[]{vertexIndices[0], vertexIndices[i], vertexIndices[i + 1]});
                if (hasUvs) {
                    faceUvIndices.add(new int[]{uvIndices[0], uvIndices[i], uvIndices[i + 1]});
                    continue;
                }
                faceUvIndices.add(null);
            }
        }
    }

    public static class ObjParseException
    extends Exception {
        public ObjParseException(String message) {
            super(message);
        }
    }

    public record ObjMesh(List<float[]> vertices, List<float[]> uvCoordinates, List<int[]> faces, List<int[]> faceUvIndices, List<String> faceMaterials, @Nullable String mtlLib) {
        public float[] getBounds() {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -3.4028235E38f;
            float maxY = -3.4028235E38f;
            float maxZ = -3.4028235E38f;
            for (float[] v : this.vertices) {
                minX = Math.min(minX, v[0]);
                minY = Math.min(minY, v[1]);
                minZ = Math.min(minZ, v[2]);
                maxX = Math.max(maxX, v[0]);
                maxY = Math.max(maxY, v[1]);
                maxZ = Math.max(maxZ, v[2]);
            }
            return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
        }

        public float getHeight() {
            float[] bounds = this.getBounds();
            return bounds[4] - bounds[1];
        }

        public boolean hasMaterials() {
            return this.mtlLib != null && !this.faceMaterials.isEmpty() && this.faceMaterials.stream().anyMatch(m -> m != null);
        }

        public boolean hasUvCoordinates() {
            return !this.uvCoordinates.isEmpty() && this.faceUvIndices.stream().anyMatch(uv -> uv != null);
        }

        public void transformZUpToYUp() {
            for (float[] v : this.vertices) {
                float z;
                float y = v[1];
                v[1] = z = v[2];
                v[2] = -y;
            }
        }

        public void transformXUpToYUp() {
            for (float[] v : this.vertices) {
                float y;
                float x = v[0];
                v[0] = y = v[1];
                v[1] = x;
            }
        }
    }
}

