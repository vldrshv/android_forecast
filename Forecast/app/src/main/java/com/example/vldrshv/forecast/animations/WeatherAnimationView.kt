package com.example.vldrshv.forecast.animations

import android.animation.TimeAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.example.vldrshv.forecast.R

import kotlin.random.Random

/**
 * Continuous animation where stars slide from the bottom to the top
 * Created by Patrick Ivarsson on 7/23/17.
 */
class WeatherAnimationView (
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        var defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    
    init {
        init()
    }
    
    private class WeatherObj {
        var x : Float = 0f
        var y : Float = 0f
        var scale : Float = 0f
        var alpha : Float = 0f
        var speed : Float = 0f
    }
    
    private val BASE_SPEED_DP_PER_S = 200
    private val COUNT = 50
    private val SEED = 1337
    
    /** The minimum scale of a star */
    private val SCALE_MIN_PART = 0.45f
    /** How much of the scale that's based on randomness */
    private val SCALE_RANDOM_PART = 0.55f
    /** How much of the alpha that's based on the scale of the star */
    private val ALPHA_SCALE_PART = 0.5f
    /** How much of the alpha that's based on randomness */
    private val ALPHA_RANDOM_PART = 0.5f
    
    private val mStars = Array(COUNT) { i -> WeatherObj() }
    private val mRnd = Random(SEED)
    
    var mTimeAnimator : TimeAnimator? = null
    private var mDrawable : Drawable? = null
    
    private var mBaseSpeed : Float = 0f
    private var mBaseSize : Float = 0f
    private var mCurrentPlayTime : Long = 0L
    
    
    /** @see View#View(Context, AttributeSet) */
    constructor(context: Context, attrs: AttributeSet ) : this (context, attrs, 0, 0) {
        init()
    }
    
    /** @see View#View(Context, AttributeSet, int) */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int ) : this(context, attrs, defStyle, 0 ) {
        init()
    }
    
    private fun init() {
        mDrawable = ContextCompat.getDrawable(context, defStyleRes)//, R.drawable.snow)//R.drawable.star)
        mBaseSize = Math.max(mDrawable!!.intrinsicWidth, mDrawable!!.intrinsicHeight) / 4f
        mBaseSpeed = BASE_SPEED_DP_PER_S * resources.displayMetrics.density
    }
    
    /**
     * Initialize the given weatherObj by randomizing it's position, scale and alpha
     * @param weatherObj the weatherObj to initialize
     * @param viewWidth the view width
     * @param viewHeight the view height
     */
    private fun initializeObj(weatherObj: WeatherObj, viewWidth: Int, viewHeight: Int) : WeatherObj {
        // Set the scale based on a min value and a random multiplier
        weatherObj.scale = SCALE_MIN_PART + SCALE_RANDOM_PART * mRnd.nextFloat()
        
        // Set X to a random value within the width of the view
        weatherObj.x = viewWidth * mRnd.nextFloat()
        
        // Set the Y position
        // Start at the bottom of the view
        weatherObj.y = -viewHeight.toFloat() / 3
        // The Y value is in the center of the weatherObj, add the size
        // to make sure it starts outside of the view bound
        weatherObj.y += weatherObj.scale * mBaseSize
        // Add a random offset to create a small delay before the
        // weatherObj appears again.
        weatherObj.y += viewHeight * mRnd.nextFloat() / 4f
        
        // The alpha is determined by the scale of the weatherObj and a random multiplier.
        weatherObj.alpha = ALPHA_SCALE_PART * weatherObj.scale + ALPHA_RANDOM_PART * mRnd.nextFloat()
        // The bigger and brighter a weatherObj is, the faster it moves
        weatherObj.speed = mBaseSpeed * weatherObj.alpha * weatherObj.scale
        
        return weatherObj
    }
    
    protected override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        
        // The starting position is dependent on the size of the view,
        // which is why the model is initialized here, when the view is measured.
        mStars.forEach {initializeObj(it, width, height)}
    }
    
    protected override fun onDraw(canvas: Canvas) {
        val viewHeight: Int = height
        for (weatherObj: WeatherObj in mStars) {
            // Ignore the weatherObj if it's outside of the view bounds
            val starSize: Float  = weatherObj.scale * mBaseSize
            if (weatherObj.y + starSize < 0 || weatherObj.y - starSize > viewHeight) {
//            if (weatherObj.y - starSize > 0 || weatherObj.y + starSize < viewHeight) {
                continue
            }
            
            // Save the current canvas state
            val save: Int = canvas.save()
            
            // Move the canvas to the center of the weatherObj
            canvas.translate(weatherObj.x, weatherObj.y)
            
            // Rotate the canvas based on how far the weatherObj has moved
//            val progress: Float = (weatherObj.y + starSize) / viewHeight
//            val progress: Float = (weatherObj.y - starSize) / viewHeight
//            canvas.rotate(360 * progress)
            
            // Prepare the size and alpha of the drawable
            val size: Int = Math.round(starSize)
            mDrawable!!.setBounds(-size, -size, size, size)
            mDrawable!!.alpha = Math.round(255 * weatherObj.alpha)
            
            // Draw the weatherObj to the canvas
            mDrawable!!.draw(canvas)
            
            // Restore the canvas to it's previous position and rotation
            canvas.restoreToCount(save)
        }
    }
    
    private val onTimeUpdateListener =
            TimeAnimator.TimeListener() { timeAnimator: TimeAnimator, totalTime: Long, deltaTime: Long ->
        if (isLaidOut) {
            // Ignore all calls before the view has been measured and laid out.
            updateState(deltaTime)
            invalidate()
        }
    }
    
    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mTimeAnimator = TimeAnimator()
        mTimeAnimator!!.setTimeListener(onTimeUpdateListener)
        mTimeAnimator!!.start()
    }
    
    protected override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mTimeAnimator!!.cancel()
        mTimeAnimator!!.setTimeListener(null)
        mTimeAnimator!!.removeAllListeners()
        mTimeAnimator = null
    }
    
    /**
     * Pause the animation if it's running
     */
    fun pause() {
        if (mTimeAnimator != null && mTimeAnimator!!.isRunning) {
            // Store the current play time for later.
            mCurrentPlayTime = mTimeAnimator!!.currentPlayTime
            mTimeAnimator!!.pause()
        }
    }
    
    /**
     * Resume the animation if not already running
     */
    fun resume() {
        if (mTimeAnimator != null && mTimeAnimator!!.isPaused) {
            mTimeAnimator!!.start()
            // Why set the current play time?
            // TimeAnimator uses timestamps internally to determine the delta given
            // in the TimeListener. When resumed, the next delta received will the whole
            // pause duration, which might cause a huge jank in the animation.
            // By setting the current play time, it will pick of where it left off.
            mTimeAnimator!!.currentPlayTime = mCurrentPlayTime
        }
    }
    
    /**
     * Progress the animation by moving the stars based on the elapsed time
     * @param deltaMs time delta since the last frame, in millis
     */
    private fun updateState(deltaMs: Long) {
        // Converting to seconds since PX/S constants are easier to understand
        val deltaSeconds: Float = deltaMs / 1000f
        val viewWidth: Int = width
        val viewHeight: Int = height
        
        for (weatherObj: WeatherObj in mStars) {
            // Move the weatherObj based on the elapsed time and it's speed
            weatherObj.y += weatherObj.speed * deltaSeconds
//            weatherObj.y -= weatherObj.speed * deltaSeconds
            
            // If the weatherObj is completely outside of the view bounds after
            // updating it's position, recycle it.
            val size: Float = weatherObj.scale * mBaseSize;
//            if (weatherObj.y + size < 0) {
            if (weatherObj.y - size > viewHeight) {
                initializeObj(weatherObj, viewWidth, viewHeight)
            }
        }
    }
    
    
}