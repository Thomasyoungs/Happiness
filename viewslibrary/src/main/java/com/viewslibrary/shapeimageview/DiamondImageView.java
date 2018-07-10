package com.viewslibrary.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.viewslibrary.R;
import com.viewslibrary.shapeimageview.shader.ShaderHelper;
import com.viewslibrary.shapeimageview.shader.SvgShader;

public class DiamondImageView extends ShaderImageView {

    public DiamondImageView(Context context) {
        super(context);
    }

    public DiamondImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiamondImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_diamond);
    }
}
