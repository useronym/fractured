precision mediump float;

uniform float time;

varying vec2 TexCoord;

void main()
{
    float sine = 1.0 - abs((sin(mod(time, 31.4) * TexCoord.x * 3.14)) - (1.0 - TexCoord.y * 2.0));
    sine = pow(sine, 10.0);
    gl_FragColor = vec4(sine, 0.0, 0.0, 1.0);
}
