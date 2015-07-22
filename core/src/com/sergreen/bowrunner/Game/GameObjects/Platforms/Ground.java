package com.sergreen.bowrunner.Game.GameObjects.Platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sergreen.bowrunner.Game.GameObjects.GameObject;
import com.sergreen.bowrunner.Utils.Global;
import com.sergreen.bowrunner.Utils.BayazitDecomposer;

/**
 * Created on 13.02.2015 [SerGreen]
 */
public class Ground extends GameObject {
    protected Sprite sprite;
    protected Vector2 position;
    private float[] vertices;

    public Ground(Vector2 position, float[] vertices, World world, String type) {
        super("ground");

        this.position = position;
        this.vertices = vertices;

        try {
            sprite = new Sprite(Global.assetManager.get("textures/ground/" + type + ".png", Texture.class));

            float width = vertices[vertices.length - 4] - vertices[2];  //width of ground in meters
            sprite.setScale(width / sprite.getWidth());
            sprite.setPosition(position.x, getTopMostYPosition() + 5f);
            sprite.setOrigin(0, sprite.getHeight() - 2);
        }
        catch (GdxRuntimeException e) { Gdx.app.log("Error", "No sprite found [" + type + ".png]"); }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(this.position);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Global.GROUND_BIT;
        fixtureDef.filter.maskBits = Global.PLAYER_BIT | Global.ARROW_BIT | Global.TARGET_BIT;

        createBodyFixturesBayazit(fixtureDef, vertices);
    }

    public Ground(String id) {
        super(id);
    }

    private void createBodyFixturesBayazit(FixtureDef fixtureDef, float[] vertices) {
        Array<Vector2> v2v = getVector2Vertices(vertices);
        Array<Array<Vector2>> polygons = BayazitDecomposer.ConvexPartition(v2v);
        PolygonShape shape = new PolygonShape();

        for (int i = 0; i < polygons.size; i++) {
            Array<Vector2> polygon = polygons.get(i);
            Vector2[] verts = new Vector2[polygon.size];
            for (int j = 0; j < polygon.size; j++)
                verts[j] = polygon.get(j);

            shape.set(verts);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        shape.dispose();
    }

    private Array<Vector2> getVector2Vertices(float[] vertices) {
        Array<Vector2> v2v = new Array<Vector2>();
        for (int i = 0; i < vertices.length - 1; i+=2)
            v2v.add(new Vector2(vertices[i], vertices[i+1]));
        return invertVertices(v2v);
    }

    private Array<Vector2> invertVertices(Array<Vector2> vertices) {
        Array<Vector2> inverted = new Array<Vector2>();
        for(int i = vertices.size-1; i >= 0; i--)
            inverted.add(vertices.get(i));

        return inverted;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (sprite != null) {
            batch.draw(sprite,
                    sprite.getX() - sprite.getOriginX(), sprite.getY() - sprite.getOriginY(),
                    sprite.getOriginX(), sprite.getOriginY(),
                    sprite.getWidth(), sprite.getHeight(),
                    sprite.getScaleX(), sprite.getScaleY(),
                    sprite.getRotation());
        }
    }

    public Vector2 getRightmostPoint() {
        //vertices stored like [x1, y1, x2, y2, x3, y3, ...], so last vertex [x, y] is [length-2, length-1]
        //penultimate vertex is the top-right vertex, which is [length-4, length-3]
        int verticesAmount = vertices.length;
        return new Vector2(position.x + vertices[verticesAmount-4],
                           position.y + vertices[verticesAmount-3]);
    }

    // returns true if object is close to right edge of screen (but not yet on screen)
    // used for in-time world generation
    public boolean isCloseToCameraRightEdge(OrthographicCamera camera) {
        return getRightmostPoint().x < camera.position.x + camera.viewportWidth;
    }

    // Y position for sprite is the Y of top-most point of the entire body
    public float getTopMostYPosition() {
        float topMost = vertices[3];    // 3 is Y for 2nd point (1st point is always lower than 2nd, so it's for the sake of optimisation)
        for (int i = 5; i < vertices.length; i+=2) {     // i=5 is Y of 3rd point
            if(vertices[i] > topMost)
                topMost = vertices[i];
        }
        return position.y + topMost;
    }

    @Override
    public void update() { }
}
