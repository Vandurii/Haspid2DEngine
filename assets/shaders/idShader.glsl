//#type vertexShader
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 2) in vec2 aTexCords;
layout (location = 3) in float aTexID;
layout (location = 4) in float aObjectID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec2 fTexCords;
out float fTexID;
out float fObjectID;

void main(){
    fTexCords = aTexCords;
    fTexID = aTexID;
    fObjectID = aObjectID;

    gl_Position = uProjection * uView * vec4(aPos, 1);
}

//#type fragmentShader
#version 330 core

uniform sampler2D uTextures[8];

in vec2 fTexCords;
in float fTexID;;
in float fObjectID;

out vec4 color;

void main(){
    int id = int(fTexID);
    vec4 texColor = texture(uTextures[id], fTexCords);

    if(texColor.a < 0.5){
        discard;
    }
    color = vec4(fObjectID, fObjectID, fObjectID, fObjectID);
}