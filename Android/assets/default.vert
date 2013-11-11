attribute vec4 a_position;
attribute vec2 a_texCoord0;

varying vec2 TexCoord;

void main()
{
    TexCoord = a_texCoord0;
    gl_Position= a_position;
}
