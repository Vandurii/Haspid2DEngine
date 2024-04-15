//#type vertexShader
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCords;
layout (location = 3) in float aTexID;
//layout (location = 4) in float aObjectID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCords;
out float fTexID;

void main(){
    fColor = aColor;
    fTexCords = aTexCords;
    fTexID = aTexID;

    gl_Position = uProjection * uView * vec4(aPos, 1);
}

//#type fragmentShader
#version 330 core

uniform sampler2D uTextures[8];

in vec2 fTexCords;
in float fTexID;
in vec4 fColor;

out vec4 color;

void main(){
    int id = int(fTexID);
    if(id == 0){
        color = fColor;
    }else{
        color = fColor * texture(uTextures[id], fTexCords);
    }
}