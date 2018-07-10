package com.viewslibrary.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.viewslibrary.R;
import com.viewslibrary.shapeimageview.shader.ShaderHelper;
import com.viewslibrary.shapeimageview.shader.SvgShader;


public class HexagonImageView extends ShaderImageView {

    public HexagonImageView(Context context) {
        super(context);
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_hexagon);
    }
}
