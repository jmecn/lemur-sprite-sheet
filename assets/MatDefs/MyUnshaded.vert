#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
#define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;
#ifdef HAS_POINTSIZE
uniform float m_PointSize;
#endif
#ifdef HAS_TILING_OFFSET
uniform vec4 m_TilingOffset;
#endif

void main(){
    #ifdef NEED_TEXCOORD1
    texCoord1 = inTexCoord;
    #ifdef HAS_TILING_OFFSET
    // Apply tiling and offset to texcoord1
    texCoord1 = texCoord1 * m_TilingOffset.xy + m_TilingOffset.zw;
    #endif
    #endif

    #ifdef SEPARATE_TEXCOORD
    texCoord2 = inTexCoord2;
    #ifdef HAS_TILING_OFFSET
    // Apply tiling and offset to texcoord2
    texCoord2 = texCoord2 * m_TilingOffset.xy + m_TilingOffset.zw;
    #endif
    #endif

    #ifdef HAS_VERTEXCOLOR
    vertColor = inColor;
    #endif

    #ifdef HAS_POINTSIZE
    gl_PointSize = m_PointSize;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);

    #ifdef NUM_MORPH_TARGETS
    Morph_Compute(modelSpacePos);
    #endif

    #ifdef NUM_BONES
    Skinning_Compute(modelSpacePos);
    #endif

    gl_Position = TransformWorldViewProjection(modelSpacePos);
}