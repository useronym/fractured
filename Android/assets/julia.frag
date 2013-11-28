precision highp float;

uniform vec2 u_c;
uniform vec2 u_translation;
uniform float u_zoom, u_zoom_half;

varying vec2 TexCoord;

void main()
{
    int iter = 100;
    vec2 c = u_c;
    vec2 z;
    z.x = ((TexCoord.x + u_translation.x) * u_zoom - u_zoom_half) * 5.0;
    z.y = ((TexCoord.y + u_translation.y) * u_zoom - u_zoom_half) * 5.0;

    int i;
    for(i= 0; i < iter; i++)
    {
        float x= (z.x * z.x - z.y * z.y) + c.x;
        float y= (2.0 * z.x * z.y) + c.y;

        if((x*x + y*y) > 4.0) break;
        z.x= x;
        z.y= y;
    }

    float col= float(i) / float(iter);
    if(i == iter) col= 0.0;

    gl_FragColor = vec4(0.0, tan(col), 0.0, 1.0);
}
