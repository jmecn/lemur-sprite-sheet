package net.jmecn.gui.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.*;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DefaultCursorListener;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;

import java.nio.FloatBuffer;

/**
 * desc:
 *
 * @author yanmaoyuan
 * @date 2023/8/15
 */
public class GuiState extends BaseAppState {

    // 这是修改过的 Unshaded.j3md，增加了两个参数：Tiling 和 Offset
    private static final String MY_UNSHADED = "/MatDefs/MyUnshaded.j3md";
    public static final String MY_TEXTURE = "Textures/Steampunk_UI_Alternative_Colors_1.png";
    private static final String MISSING_TEXTURE = "/Common/Textures/MissingTexture.png";

    private static final String STYLE = "glass";
    private static final String FONT = "Font/indoor.fnt";

    private VersionedReference<Double> tilingX;
    private VersionedReference<Double> tilingY;
    private VersionedReference<Double> offsetX;
    private VersionedReference<Double> offsetY;
    private VersionedReference<Boolean> repeatX;
    private VersionedReference<Boolean> repeatY;

    private Label labelTilingX;
    private Label labelTilingY;
    private Label labelOffsetX;
    private Label labelOffsetY;

    private Node guiNode;

    private AssetManager assetManager;

    private Vector2f resolution;// 屏幕分辨率

    private Geometry bg;
    private Geometry wire;
    private Geometry img1;
    private Geometry img2;

    private Texture texture;

    @Override
    protected void initialize(Application app) {
        assetManager = app.getAssetManager();
        assetManager.registerLocator("assets", FileLocator.class);

        guiNode = ((SimpleApplication) app).getGuiNode();

        texture = loadTexture();

        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(STYLE);

        Camera cam = app.getCamera();
        resolution = new Vector2f(cam.getWidth(), cam.getHeight());

        // BitmapFont font = assetManager.loadFont("Font/indoor.fnt");
        // GuiGlobals.getInstance().getStyles().setDefault(font);
        Container window = new Container(STYLE);
        window.addChild(new Label("Image Property", new ElementId("title"), STYLE));

        Container panel = new Container(STYLE);
        tilingX = addSlider(panel, "TilingX:", 0.01, 5, 1, labelTilingX = new Label("1", STYLE));
        tilingY = addSlider(panel, "TilingY:", 0.01, 5, 1, labelTilingY = new Label("1", STYLE));
        offsetX = addSlider(panel, "OffsetX:", -1, 1, 0, labelOffsetX = new Label("0", STYLE));
        offsetY = addSlider(panel, "OffsetY:", -1, 1, 0, labelOffsetY = new Label("0", STYLE));

        Container repeat = new Container(STYLE);
        repeat.setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None));
        repeatX = repeat.addChild(new Checkbox("RepeatX", STYLE)).getModel().createReference();
        repeatY = repeat.addChild(new Checkbox("RepeatY", STYLE)).getModel().createReference();
        panel.addChild(repeat);

        window.addChild(panel);
        window.setLocalTranslation(10f, resolution.getY() - 10f,  20f);
        guiNode.attachChild(window);

        Node bgNode = new Node("bgNode");
        bgNode.setLocalTranslation(600, 120, 0);
        bgNode.scale(480);
        guiNode.attachChild(bgNode);

        bg = createBg();
        bg.setLocalTranslation(0, 0, 0);
        bgNode.attachChild(bg);

        wire = createWire();
        wire.setLocalTranslation( 0, 0, 2 );
        bgNode.attachChild( wire );

        Node previewNode = new Node("preview");
        previewNode.setLocalTranslation(10, 10, 10);
        previewNode.scale(200);
        guiNode.attachChild(previewNode);

        img1 = createImg();
        img1.setLocalTranslation(0, 1, 1);
        previewNode.attachChild(img1);

        img2 = createImg2();
        img2.setLocalTranslation(0, 0, 1);
        previewNode.attachChild(img2);

        CursorEventControl.addListenersToSpatial(previewNode, new DragHandler());
    }

    private Texture loadTexture() {
        try {
            return assetManager.loadTexture(MY_TEXTURE);
        } catch (AssetNotFoundException e) {
            return assetManager.loadTexture(MISSING_TEXTURE);
        }
    }

    private Geometry createBg() {
        Quad quad = new Quad(1, 1);
        Geometry geom = new Geometry("bg", quad);
        Material mat = new Material(assetManager, Materials.UNSHADED);
        mat.setTexture("ColorMap", texture);
        geom.setMaterial(mat);
        return geom;
    }

    private Geometry createWire() {
        Quad quad = new Quad(1, 1);
        Geometry geom = new Geometry("wire", quad);
        Material mat = new Material(assetManager, Materials.UNSHADED);
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setWireframe(true);
        geom.setMaterial(mat);
        return geom;
    }

    private Geometry createImg() {
        Quad quad = new Quad(1, 1);
        Geometry geom = new Geometry("img1", quad);
        Material mat = new Material(assetManager, Materials.UNSHADED);
        mat.setTexture("ColorMap", texture);
        mat.setFloat("AlphaDiscardThreshold", 0.5f);
        geom.setMaterial(mat);
        return geom;
    }

    private Geometry createImg2() {
        Quad quad = new Quad(1, 1);
        Geometry geom = new Geometry("img2", quad);
        Material mat = new Material(assetManager, MY_UNSHADED);
        mat.setTexture("ColorMap", texture);
        mat.setFloat("AlphaDiscardThreshold", 0.5f);
        geom.setMaterial(mat);
        return geom;
    }

    private VersionedReference<Double> addSlider(Container panel, String name, double min, double max, double value, Label labelValue) {
        DefaultRangedValueModel valueModel = new DefaultRangedValueModel(min, max, value);
        VersionedReference<Double> val = valueModel.createReference();

        Label labelName = new Label(name, STYLE);
        labelName.setPreferredSize(new Vector3f(60, 28, 0));

        labelValue.setText(String.format("%.2f", val.get()));
        labelValue.setPreferredSize(new Vector3f(48, 28, 0));

        Slider slider = new Slider(valueModel, STYLE);
        slider.setDelta(0.01f);
        slider.setPreferredSize(new Vector3f(200, 28, 0));
        slider.setBackground(new QuadBackgroundComponent(ColorRGBA.DarkGray,2,2, 0.02f, false));

        Container p = new Container(STYLE);
        p.setLayout(new BorderLayout());
        p.addChild(labelName, BorderLayout.Position.West);
        p.addChild(slider, BorderLayout.Position.Center);
        p.addChild(labelValue, BorderLayout.Position.East);

        panel.addChild(p);
        return val;
    }

    @Override
    public void update(float tpf) {
        boolean update = updateLabel(labelTilingX, tilingX)
                || updateLabel(labelTilingY, tilingY)
                || updateLabel(labelOffsetX, offsetX)
                || updateLabel(labelOffsetY, offsetY)
                || repeatX.update() || repeatY.update();

        if (update) {
            updateImage();
        }
    }

    private boolean updateLabel(Label label, VersionedReference<Double> valRef) {
        if (valRef.update()) {
            label.setText(String.format("%.2f", valRef.get()));
            return true;
        }
        return false;
    }

    private void updateImage() {

        // 调整纹理的包围模式：Repeat 重复，EdgeClamp 边缘拉伸
        if (repeatX.get()) {
            texture.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
        } else {
            texture.setWrap(Texture.WrapAxis.S, Texture.WrapMode.EdgeClamp);
        }
        if (repeatY.get()) {
            texture.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
        } else {
            texture.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
        }

        float tilingX = this.tilingX.get().floatValue();
        float tilingY = this.tilingY.get().floatValue();
        float offsetX = this.offsetX.get().floatValue();
        float offsetY = this.offsetY.get().floatValue();

        // 修改 Geometry 的 localTranslation 和 localScale，让用户可以观察到纹理采样的范围变化。
        wire.setLocalTranslation(offsetX, offsetY, 2f);
        wire.setLocalScale(tilingX, tilingY, 1f);

        TempVars tempVars = TempVars.get();

        Vector2f tiling = tempVars.vect2d.set(tilingX, tilingY);
        Vector2f offset = tempVars.vect2d2.set(offsetX, offsetY);

        // 通过修改mesh的uv坐标来实现
        refreshMesh(img1, tiling, offset);
        // 通过修改shader参数来实现
        refreshMaterial(img2, tiling, offset);

        tempVars.release();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    private void refreshMesh(Geometry geom, Vector2f tiling, Vector2f offset) {
        Quad mesh = (Quad) geom.getMesh();
        VertexBuffer tc = mesh.getBuffer(VertexBuffer.Type.TexCoord);

        float[] uv = new float[]{
                0, 0,
                1, 0,
                1, 1,
                0, 1};

        FloatBuffer fb = (FloatBuffer) tc.getData();
        fb.clear();
        for (int i = 0; i < uv.length; i += 2) {
            float x = uv[i];
            float y = uv[i + 1];
            x = x * tiling.getX() + offset.getX();
            y = y * tiling.getY() + offset.getY();
            fb.put(x).put(y);
        }
        fb.clear();
        tc.updateData(fb);
    }

    private void refreshMaterial(Geometry geom, Vector2f tiling, Vector2f offset) {
        Material mat = geom.getMaterial();
        mat.setVector2("Tiling", tiling);
        mat.setVector2("Offset", offset);
    }
}
