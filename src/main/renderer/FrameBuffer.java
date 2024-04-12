package main.renderer;

import main.util.Texture;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int FBO;
    private Texture texture;

    public  FrameBuffer(int width, int height){
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTexID(), 0);

        int RBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, RBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, RBO);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) throw new IllegalStateException();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
    }


    public void unBind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getTextureID(){
        return texture.getTexID();
    }

}
