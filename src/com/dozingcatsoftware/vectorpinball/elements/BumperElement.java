package com.dozingcatsoftware.vectorpinball.elements;

import static com.dozingcatsoftware.vectorpinball.util.MathUtils.asFloat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dozingcatsoftware.vectorpinball.model.Color;
import com.dozingcatsoftware.vectorpinball.model.Field;
import com.dozingcatsoftware.vectorpinball.model.IFieldRenderer;
import com.dozingcatsoftware.vectorpinball.model.Point;

/** This FieldElement subclass represents a bumper that applies an impulse to a ball when it hits. The impulse magnitude is controlled
 * by the "kick" parameter in the configuration map.
 */

public class BumperElement extends FieldElement {

    public static final String POSITION_PROPERTY = "position";
    public static final String RADIUS_PROPERTY = "radius";
    public static final String KICK_PROPERTY = "kick";

    static final Color DEFAULT_COLOR = Color.fromRGB(0, 0, 255);

	Body pegBody;
	List pegBodySet;

	float radius;
	float cx, cy;
	float kick;

	@Override public void finishCreateElement(Map<String, Object> params, FieldElementCollection collection) {
		List pos = (List)params.get(POSITION_PROPERTY);
		this.radius = asFloat(params.get(RADIUS_PROPERTY));
		this.cx = asFloat(pos.get(0));
		this.cy = asFloat(pos.get(1));
		this.kick = asFloat(params.get(KICK_PROPERTY));
	}

	@Override public void createBodies(World world) {
		pegBody = Box2DFactory.createCircle(world, cx, cy, radius, true);
		pegBodySet = Collections.singletonList(pegBody);
	}

	@Override public List<Body> getBodies() {
		return pegBodySet;
	}

	@Override public boolean shouldCallTick() {
		// needs to call tick to decrement flash counter (but can use superclass tick() implementation)
		return true;
	}


	Vector2 impulseForBall(Body ball) {
		if (this.kick <= 0.01f) return null;
		// compute unit vector from center of peg to ball, and scale by kick value to get impulse
		Vector2 ballpos = ball.getWorldCenter();
		Vector2 thisPos = pegBody.getPosition();
		float ix = ballpos.x - thisPos.x;
		float iy = ballpos.y - thisPos.y;
		float mag = (float)Math.sqrt(ix*ix + iy*iy);
		float scale = this.kick / mag;
		return new Vector2(ix*scale, iy*scale);
	}

	@Override public void handleCollision(Body ball, Body bodyHit, Field field) {
		Vector2 impulse = this.impulseForBall(ball);
		if (impulse!=null) {
			ball.applyLinearImpulse(impulse, ball.getWorldCenter(), true);
			flashForFrames(3);
		}
	}

	@Override public void draw(IFieldRenderer renderer) {
		float px = pegBody.getPosition().x;
		float py = pegBody.getPosition().y;
		renderer.fillCircle(px, py, radius, currentColor(DEFAULT_COLOR));
	}

	// Editor methods.

    @Override public void drawForEditor(IFieldRenderer renderer, boolean isSelected) {
        Color color = currentColor(DEFAULT_COLOR);
        renderer.fillCircle(cx, cy, radius, currentColor(DEFAULT_COLOR));
        if (isSelected) {
            renderer.drawLine(cx - radius, cy - radius, cx + radius, cy - radius, color);
            renderer.drawLine(cx + radius, cy - radius, cx + radius, cy + radius, color);
            renderer.drawLine(cx + radius, cy + radius, cx - radius, cy + radius, color);
            renderer.drawLine(cx - radius, cy + radius, cx - radius, cy - radius, color);
        }
    }

    @Override public boolean isPointWithinDistance(Point point, double distance) {
        return point.distanceTo(Point.fromXY(cx, cy)) <= this.radius + distance;
    }

    @Override public void handleDrag(Point point, Point deltaFromStart, Point deltaFromPrevious) {
        // TODO: handle resizing as well as moving.
        cx += deltaFromPrevious.x;
        cy += deltaFromPrevious.y;
    }

    @Override public Map<String, Object> getPropertyMap() {
        Map<String, Object> properties = mapWithDefaultProperties();
        properties.put(RADIUS_PROPERTY, this.radius);
        properties.put(POSITION_PROPERTY, Arrays.asList(this.cx, this.cy));
        if (this.kick != 0) {
            properties.put(KICK_PROPERTY, this.kick);
        }
        return properties;
    }
}
