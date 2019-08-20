package me.sensingself.sensingsugar.Engine.libs;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import java.util.Arrays;

import static android.util.Log.VERBOSE;
import static java.lang.Math.pow;

/**
 * Created by liujie on 12/15/17.
 */

public class if_lib {
    public String status;
    public String lf_display_status(String error)
    {
        String desc;
        if (status == "-" || status == "") {
            desc = "-";
            return desc;
        }
        String langcat   = "lf";
        String langtype  = "label-status-" + status;
        desc   = lang_getcomment1(langcat, langtype);
        String[] xrow  = desc.split(" ");
        String short_desc = xrow[0];
        return (short_desc);
    }

    public String  lang_getcomment1(String langcat, String langtype)
    {
        return langcat +  "--" +  langtype;
    }

    /*
     * Find strip glucose yyy
     */
    public float lf_find_average(Bitmap myBitmap, int xorig, int yorig)
    {

        Float[] value_array = new Float[20];
        for (int i= 0 ; i < 20; i ++){
            int x          = xorig - 10 + i;
            int y          = yorig;
            int p = myBitmap.getPixel(x, y);
            int red = Color.red(p);
            int green = Color.green(p);
            int blue = Color.blue(p);
            float value         = red - (green + blue) / 2;
            value_array[i] = value;
        }
        Arrays.sort(value_array);

        float total = 0;
        int num   = 0;
        for (int i = 1; i < 19; i++) {
            int j   = 19 - i;
            float val = value_array[j];
            total = total + val;
            num++;
        }
        float value = total / num;
        return value;
    }

    private Bitmap JPGtoRGB888(Bitmap img){
        Bitmap result = null;
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        img.getPixels(pixels,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        result = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }
    /*
     * Find red color
     */
    public float lf_find_red(Bitmap myBitmap, int xorig, int yorig)
    {
        float[] value_array         = new float[150];
        float[] average_value_array = new float[150];

        //check on a line the rgb values and then find the highest "red" value
        for (int i = 0; i < 150; i++) {
            int x          = xorig - 100 + i;
            int y          = yorig;
            int p = myBitmap.getPixel(x, y);
            int red = Color.red(p);
            int green = Color.green(p);
            int blue = Color.blue(p);
            float value         = red - (green + blue) / 2;


            double[] lchArray = RGB2Lch(red, green, blue);
            //
            // Set value to "0" if any of the filter/error conditions are met
            // TODO:set value to background instead
            //

            //arbitrary values to exclude non red colors
            if (lchArray[2] > 0.9 && lchArray[2] < 5.3) {

                if (value > 0) {
                    value = 0;
                }

            }else{
                Log.w("", "");
            }

            value_array[i] = value;
        }

        float total = 0;
        int num = 0;
        for (int i = 0; i < 19; i++) {
            float val = value_array[i];
            total = total + val;
            num++;
        }
        float value          = total / num;
        float moving_average = 0;
        moving_average = value;
        for (int i = 10; i < 140; i++) {
            moving_average = moving_average - value_array[i - 10] / num + value_array[i + 9] / num;
            if (moving_average > value) {
                value = moving_average;
            }
        }
        return value;
    }
    public double[]  RGB2Lch(int red, int green, int blue)
    {
        //Calculate L*ab values assumed D65

        //
        // convert to sRGB
        //
        double[] sRgb = sRGB(red, green, blue);
        double sred = sRgb[0];
        double sgreen = sRgb[1];
        double sblue = sRgb[2];

        //
        //convert to XYZ
        //
        double color_x = sred * 0.4124 + sgreen * 0.3576 + sblue * 0.1805;
        double color_y = sred * 0.2126 + sgreen * 0.7152 + sblue * 0.0722;
        double color_z = sred * 0.0193 + sgreen * 0.1192 + sblue * 0.9505;

        if (VERBOSE > 2) {
            //pmc_debug("RGB2Lch XYZ=color_x color_y color_z");
        }

        //
        //normalize the xyz with observer 2* and illuminant=D65
        //
        color_x = normalizeColorAxis(color_x / 0.95047);
        color_y = normalizeColorAxis(color_y / 1.00);
        color_z = normalizeColorAxis(color_z / 1.08883);

        //
        // convert to CIE l*ab
        //
        double cie_l = (116 * color_y) - 16;
        double cie_a = 500 * (color_x - color_y);
        double cie_b = 200 * (color_y - color_z);

        if (VERBOSE > 2) {
            //pmc_debug("RGB2Lch L=cie_l a=cie_a b=cie_b");
        }

        //
        // convert to CIE lch to find hue angle h (expressed in radians)
        //
        double bias = 0;
        if (cie_a < 0) {
            bias = Math.PI; //180 degrees
        } else if (cie_a > 0 && cie_b < 0) {
            bias = 2 * Math.PI; //360 degrees
        } else if (cie_a > 0 && cie_b > 0) {
            bias = 0;
        }

        //
        // add in cases for axis (a == 0 or b == 0)
        //
        double cie_h = 0;
        if (cie_a >= 0 && cie_b == 0) {
            cie_h = 0;
        } else if (cie_a < 0 && cie_b == 0) {
            cie_h = Math.PI;
        } else if (cie_a == 0 && cie_b > 0) {
            cie_h = Math.PI / 2;
        } else if (cie_a == 0 && cie_b < 0) {
            cie_h = 3 * Math.PI / 2;
        } else {
            cie_h = Math.atan(cie_b / cie_a) + bias;
        }

        //
        // calculate distance from center of color space (smaller values means it is more "grey")
        //
        double chroma = Math.pow(Math.pow(cie_a, 2) + Math.pow(cie_b, 2), 0.5);

        if (VERBOSE > 2) {
            //pmc_debug("RGB2Lch L=cie_l c=chroma h=cie_h");
        }

        double[] lchArray = new double[3];
        lchArray[0] = cie_l;
        lchArray[1] = chroma;
        lchArray[2] = cie_h;
        return lchArray;
    }
    private static double normalizeColorAxis(double axis)
    {
        double ret;
        if (axis > 0.008856) {
            ret =  Math.pow(axis, (double) 1 / 3);
        }else{
            ret =  (7.787 * axis) + (double)16 / 116;
        }
        return ret;
    }
    public double[] sRGB(int red, int green, int blue)
    {
        int[] rgb = new int[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;

        double[] srgb = new double[3];
        for (int i = 0; i < 3 ; i ++){
            int colorRGB = rgb[i];
            float color = (float) colorRGB / 255;
            double scolor;
            if (color > 0.04045) {
                scolor = pow(((0.055 + color) / 1.055), 2.4);
            } else {
                scolor = color / 12.92;
            }
            srgb[i] = scolor;
        }
        return srgb;
    }
    /*
     * color to glucose value
     *
     * glucose_value = 2 * color_value
     */
    public double lf_color_to_glucose(float color_value)
    {
        //
        //      Equation used: salivary = 6* blood + 20
        //
        //        $glucose_value = 6 * $color_value + 20;
        //        $glucose_value = ($color_value - 2.602)/0.07607;

        double glucose_value = 7.7 * color_value - 33.6;
        //double glucose_value = 10.846 * (color_value - 7.0);

        if (glucose_value < 50) {
            glucose_value = 50;
        }

        //pmc_debug("color_to_glucose: color_value=$color_value,glucose_value=$glucose_value");
        return glucose_value;
    }

    /*
     * glucose value to result
     *     < 0 -> invalid
     *     0-110 -> normal
     *     110-160 -> shigh
     *     >160 -> high
     */
    public String lf_glucose_to_result(float glucose_value)
    {
        String result = "normal";
        if (glucose_value >= 110 && glucose_value < 160) {
            result = "shigh";
            return result;
        }
        if (glucose_value >= 160) {
            result = "high";
            return result;
        }
        if (glucose_value < 0) {
            result = "invalid";
            return result;
        }
        return result;
    }

    /*
     * glucose value to blood (both in uM)
     * Blood = 1800+ 62 * salivary glucose
     */
    public int lf_glucose_to_blood(int glucose_value)
    {
        // 2014-12-14
        int blood = 1800 + 62 * glucose_value;
        return blood;
    }
}
