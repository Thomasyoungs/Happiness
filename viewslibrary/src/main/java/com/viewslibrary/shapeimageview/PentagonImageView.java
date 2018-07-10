package com.viewslibrary.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.viewslibrary.R;
import com.viewslibrary.shapeimageview.shader.ShaderHelper;
import com.viewslibrary.shapeimageview.shader.SvgShader;


public class PentagonImageView extends ShaderImageView {

    public PentagonImageView(Context context) {
        super(context);
    }

    public PentagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PentagonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_pentagon);
    }
}
