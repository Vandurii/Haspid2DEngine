package main.renderer;

import static org.lwjgl.opengl.GL30.*;

public class IDBuffer {
    int FBO;
    int textureID;

    public IDBuffer(int width, int height){
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, FBO);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);

        glEnable(GL_TEXTURE_2D);

        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) throw new IllegalStateException("Unable to create frame buffer.");

        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
    }

    public void bind(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, FBO);
    }

    public void unbind(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, GL_NONE);
    }

    public float readIDFromPixel(int x,  int y){;
        glBindFramebuffer(GL_READ_FRAMEBUFFER, FBO);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pix = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pix);


        return pix[0];
    }
}
