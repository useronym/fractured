precision mediump float;

uniform float time;

varying vec2 TexCoord;

void main()
{
    vec2 z, c;
    int iter = 400;
    c = vec2(0.36, 0.36);
    z.x= ((TexCoord.x+0.2)*0.1 - 0.05) * 5.0;
    z.y= ((TexCoord.y+0.7)*0.1 - 0.05) * 5.0;

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
