#version 150

flat in vec4 vertexColor; // Using "flat" here tells OpenGL not to interpolate colour between vertices

uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
