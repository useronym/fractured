precision highp float;

#define ITER 150

uniform vec2 u_c;
uniform vec2 u_translation;
uniform float u_zoom, u_aspectratio;
uniform sampler2D gradient;

varying vec2 TexCoord;

vec2 cmul(in vec2 a, in vec2 b)
{
    float r, i;
    r = a.x*b.x - a.y*b.y;
    i = a.x*b.y + a.y*b.x;
    return vec2(r, i);
}

vec2 cexp(in vec2 a)
{
    return pow(2.71828, a.x) * vec2(cos(a.y), sin(a.y));
}

void main()
{
    vec2 z = ((TexCoord + u_translation) * u_zoom) - vec2(u_zoom / 2.0) * vec2(1.0, u_aspectratio);
    int i = 0;

    for(; i < ITER; i++)
    {
        vec2 newz = cexp(cmul(z, z));
        newz -= u_c;

        if (length(newz) > 2.0) break;
        z = newz;
    }

    float escape = 0.0;
    if (i < ITER)
    {
        escape = float(i) / float(ITER);
    }

    vec3 color = texture2D(gradient, vec2(escape, 0.5)).rgb;
    gl_FragColor = vec4(color, 1.0);
}
