precision highp float;

#define ITER 150

uniform vec2 u_c;
uniform vec2 u_translation;
uniform float u_zoom, u_aspectratio;
uniform sampler2D gradient;

varying vec2 TexCoord;

void main()
{
    vec2 c = u_c;
    vec2 z;
    z.x = ((TexCoord.x + u_translation.x) * u_zoom) - u_zoom / 2.0;
    z.y = ((TexCoord.y + u_translation.y) * u_zoom) - (u_zoom / 2.0) * u_aspectratio;

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

    vec3 color = texture2D(gradient, vec2(escape, 0.5)).rgb;
    gl_FragColor = vec4(color, 1.0);
}
