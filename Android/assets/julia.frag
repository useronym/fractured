precision highp float;

#define ITER 100

uniform vec2 u_c;
uniform vec2 u_translation;
uniform float u_zoom, u_zoom_half;

varying vec2 TexCoord;

void main()
{
    vec2 c = u_c;
    vec2 z;
    z.x = ((TexCoord.x + u_translation.x) * u_zoom - u_zoom_half);// * 5.0;
    z.y = ((TexCoord.y + u_translation.y) * u_zoom - u_zoom_half);// * 5.0;

    int i = 0;
    for(; i < ITER; i++)
    {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (2.0 * z.x * z.y) + c.y;

        if ((x*x + y*y) > 4.0) break;
        z.x= x;
        z.y= y;
    }

    float escape = float(i) / float(ITER);
    if (i == ITER) escape = 0.0;

    gl_FragColor = vec4(0.0, escape, 0.0, 1.0);
}
