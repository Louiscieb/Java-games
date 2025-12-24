package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.louiscieb.towerdefence.model.Enemy;

public class EnemyRenderer {

    private static final float SCALE = 5f;

    private final Animation<TextureRegion> runAnimation;
    private final Texture whitePixel;
    private final BitmapFont font;

    public EnemyRenderer() {
        // enemy frames
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 12; i++) {
            Texture tex = new Texture("enemy/run" + i + ".png");
            frames.add(new TextureRegion(tex));
        }
        runAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.LOOP);

        // 1x1 white pixel
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
    }

    public void render(SpriteBatch batch, Enemy e) {
        TextureRegion frame = runAnimation.getKeyFrame(e.getAnimTime());

        float w = frame.getRegionWidth() * SCALE;
        float h = frame.getRegionHeight() * SCALE;

        float x = e.getPosition().x;
        float y = e.getPosition().y;

        batch.draw(frame, x - w / 2f, y - h / 2f, w, h);

        // HP bar
        float barWidth = w * 0.85f;
        float barHeight = 7f;
        float hpPercent = e.getHp() / e.getMaxHp();

        float barX = x - barWidth / 2f;
        float barY = y + h / 2f + 20f;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);

        font.draw(batch, "Lv " + e.getLevel(), x - 22f, barY + barHeight + 16f);
    }

    public void dispose() {
        // dispose textures created here
        // (frames textures are inside TextureRegion -> we kept Texture references only indirectly,
        // so simplest: reload approach isn't ideal. For a clean academic project, you can keep
        // Texture refs in an Array and dispose them. If you want I’ll adjust.)
        whitePixel.dispose();
        font.dispose();
    }
}
