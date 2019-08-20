package me.sensingself.sensingsugar.Engine.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import me.sensingself.sensingsugar.Engine.models.Dimension;
import me.sensingself.sensingsugar.Engine.models.Image;
import me.sensingself.sensingsugar.Engine.models.Plastic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.util.Log.VERBOSE;
import static me.sensingself.sensingsugar.Engine.libs.errors.ERR_FIND_BOTTOM_SIDE_OF_STRIP;
import static me.sensingself.sensingsugar.Engine.libs.errors.ERR_FIND_LEFT_SIDE_OF_STRIP;
import static me.sensingself.sensingsugar.Engine.libs.errors.ERR_FIND_RIGHT_SIDE_OF_STRIP;
import static me.sensingself.sensingsugar.Engine.libs.errors.ERR_FIND_TOP_SIDE_OF_STRIP;
import static java.lang.Math.sqrt;

/**
 * Created by liujie on 12/15/17.
 */

public class kt3_decode_camera {

    public static final int GLOBAL_BLUE_MIN = 40;
    public static final int GLOBAL_SIGNAL_X = 700;
    public static final int GLOBAL_BACKGROUND_X = 600;

    public static final int THRESHOLD = 10;

    public static final String TAG = "Jella___";


    int temp_left_x       = 386;
    int temp_hole_left_x  = 970;
    int temp_background_x = 1465;
    int temp_signal_x     = 1638;
    int temp_ph_x         = 1685;
    int temp_hole_right_x = 1978;
    int temp_right_x      = 2450;
    int temp_top_y        = 900;
    int temp_bottom_y     = 1120;

    int temp2_width_x      = temp_right_x - temp_left_x; // temp_right_x - temp_left_x
    int temp2_height_y     = temp_bottom_y - temp_top_y; // temp_bottom_y - temp_top_y
    int temp2_hole_left_x  = (temp_hole_left_x - temp_left_x) / temp2_width_x;
    int temp2_hole_right_x = (temp_hole_right_x - temp_left_x) / temp2_width_x;
    int temp2_background_x = (temp_background_x - temp_left_x) / temp2_width_x;
    int temp2_signal_x     = (temp_signal_x - temp_left_x) / temp2_width_x;
    int temp2_ph_x         = (temp_ph_x - temp_left_x) / temp2_width_x;

    public Map<String, Object> kt3_decode(String filePath, Bitmap currentImage)
    {
        Map<String, Object> lastResult = new HashMap<String, Object>();


        Map<String, Object> dec_row = new HashMap<String, Object>();
        dec_row.put("result", "");

        Image image = new Image();
        image.init(filePath, currentImage.getWidth(), currentImage.getHeight());

        Map<String, Object> plasticAndDesk = new HashMap<String, Object>();
        plasticAndDesk = kt3_extract_plastic(image, currentImage);

        Plastic plastic = new Plastic();
        plastic = (Plastic)plasticAndDesk.get("plastic");

        Bitmap dest = (Bitmap)plasticAndDesk.get("dest");

        int rotate = (int)image.getRotation();

        Object check_status = plastic.getStatus();


        if (plastic.hasError()) {
            Log.w(TAG,"kt3_decode ended status=check_status");
            dec_row.put("dec_status", check_status);
            dec_row.put("result", "|Error|1001| check_status=" + check_status);

            lastResult.put("dec_row", dec_row);
            lastResult.put("plastic", plastic);
            lastResult.put("image", image);


            Map<String, Object> plastic_parm_error =(Map<String, Object>) plastic.toArray();
            int error = (Integer) plastic_parm_error.get("plastic_error");
            String errorMsg = "";
            switch (error) {
                case ERR_FIND_LEFT_SIDE_OF_STRIP:  errorMsg = "Error finding left side of Strip";
                    break;
                case ERR_FIND_RIGHT_SIDE_OF_STRIP:  errorMsg = "Error finding right side of Strip";
                    break;
                case ERR_FIND_TOP_SIDE_OF_STRIP:  errorMsg = "Error finding top side of Strip";
                    break;
                case ERR_FIND_BOTTOM_SIDE_OF_STRIP:  errorMsg = "Error finding side side of Strip";
                    break;
                default: errorMsg = "Invalid value";
                    break;
            }
            lastResult.put("success", false);
            lastResult.put("err", errorMsg);

            return lastResult;
        }

        if (rotate == -1) {
            //put in a place holder error number here
            //TODO:create an error number for unable to rotate

            //TODO:CURRENTLY IGNORING ROTATE ERRORS
            Log.w(TAG,"kt3_decode could not execute rotate code rotate=rotate");
        }

        //
        // Decode Color
        //
        int width   = 300;
        int height  = 1200;

        dec_row = kt3_get_result(dest, width, height);
        int status  = kt3_check_quality(dec_row);

        dec_row.put("dec_status", status);
        Log.w(TAG,"kt3_decode: status=status");

        //
        // Update record
        //
        Map<String, Object> raw_data = image.getRawData();
        Map<String, Object> plastic_parm = plastic.toArray();
        Map<String, Object> lf_result    = kt3_update_record(dec_row, raw_data, plastic_parm);
        Log.w(TAG,"kt3_decode ended");

        dec_row.put("result", "|OK|");


        lastResult.put("lf_result", lf_result);
        lastResult.put("dec_row", dec_row);
        lastResult.put("plastic", plastic);
        lastResult.put("image", image);

        lastResult.put("success", true);
        lastResult.put("err", "");

        return lastResult;
    }

    public Map<String, Object> kt3_extract_plastic(Image image, Bitmap currentImage)
    {
        Map<String, Object> returnResult = new HashMap<String, Object>();


        String filePath = image.filePath();
        int width  = image.width();
        int height = image.height();

        //
        // find plastic
        //
        Plastic plastic = new Plastic();
        plastic.init(width, height);
        plastic = kt3_find_plastic(currentImage, width, height);

        if (plastic.hasError()) {
            returnResult.put("plastic", plastic);
            returnResult.put("dest", null);
            return returnResult;
        }

        int rotate       = 0;
        Map<String, Object> raw_data     =(Map<String, Object>) image.getRawData();
        Map<String, Object> plastic_parm =(Map<String, Object>) plastic.toArray();


        int src_w = (int)(  (Float)plastic_parm.get("h2_right") - (Float) plastic_parm.get("h2_left"));
        int src_h =(int)((Float)plastic_parm.get("v2_bottom") - (Float)plastic_parm.get("v2_top"));
        int src_x = Math.round((Float)plastic_parm.get("h2_left"));
        int src_y = Math.round((Float)plastic_parm.get("v2_top"));

        // !!!!!!!!Rotationcodeisalsonotworking!!!!!
        //
        // if (src_w > src_h) {
        //     rotate = kt3_calculate_horizontal(source, raw_data, plastic_parm);
        // } else {
        //     rotate = kt3_calculate_vertical(source, raw_data, plastic_parm);
        // }
        // //
        // // update image data
        // //
        // image->setRotation(rotate);
        // image->setRawData(raw_data);

        //
        // check plastic parm
        //

        // TODO: readjust plastic parm check for kiss 3
        //
        // error = kt3_check_plastic_parm(plastic_parm);
        // pmc_debug("yyy:after check_plastic_parm, error=error");
        // if (error > 0) {
        //     // 401, 402 etc
        //     err_msg = lf_display_status(error);
        //     if (VERBOSE > 0) {
        //         msg = "Error #" . error . " - " . err_msg;
        //         pmc_debug(msg);
        //     }
        //     check_status = error;
        //     dest         = array();
        //     //return(dest);
        // }

        //
        // Resize the image
        //
        Dimension src    = new Dimension();
        src.init(src_x, src_y, src_w, src_h);
        Dimension dest   = new Dimension();
        dest.init(0, 0, 300, 1200);

        Bitmap imagePath = image.resize(src, dest, currentImage);

        //TODO:add this back in
        returnResult.put("plastic", plastic);
        returnResult.put("dest", imagePath);
        return returnResult;
    }
    public Plastic kt3_find_plastic(Bitmap myBitmap, int width, int height)
    {
        // if (VERBOSE > 1) {
        //pmc_debug("kt3_find_plastic: width=width,height=height");
        //}

        Plastic plastic = new Plastic();
        plastic.init(width, height);
        //
        // TODO: apply gaussian filter then edge detect to find blue cassette edges
        //

        // starts with finding top and bottom with mid x
        float mid_x  = (float)width / 2;
        float top    = kt3_find_blue_line_top(myBitmap, height, Math.round(mid_x));
        float bottom = kt3_find_blue_line_bottom(myBitmap, height, Math.round(mid_x));
        if (top < 0) {
            //pmc_debug("XERROR " . ERR_FIND_TOP_SIDE_OF_STRIP . "-Cannot find the top side of strip");
            plastic.setError(ERR_FIND_TOP_SIDE_OF_STRIP);
            return plastic;
        }
        if (bottom < 0) {
            //pmc_debug("XERROR " . ERR_FIND_BOTTOM_SIDE_OF_STRIP . "-Cannot find the bottom side of strip");
            plastic.setError(ERR_FIND_BOTTOM_SIDE_OF_STRIP);
            return plastic;
        }
        float mid_y     = (bottom + top) / 2;
        float mid_left  = kt3_find_blue_line_left(myBitmap, width, Math.round(mid_y));
        float mid_right = kt3_find_blue_line_right(myBitmap, width, Math.round(mid_y));

        if (mid_left < 0) {
            //pmc_debug("XERROR " . ERR_FIND_LEFT_SIDE_OF_STRIP . "-Cannot find the left side of strip");
            plastic.setError(ERR_FIND_LEFT_SIDE_OF_STRIP);
            return plastic;
        }
        if (mid_right < 0) {
            //pmc_debug("XERROR " . ERR_FIND_RIGHT_SIDE_OF_STRIP . "-Cannot find the left side of strip");
            plastic.setError(ERR_FIND_RIGHT_SIDE_OF_STRIP);
            return plastic;
        }

        float mid_mid    = mid_left + (mid_right - mid_left) / 2;
        float mid_top    = kt3_find_blue_line_top(myBitmap, height, (int)mid_mid);
        float mid_bottom = kt3_find_blue_line_bottom(myBitmap, height, (int)mid_mid);
        if (VERBOSE > 1) {
            //pmc_debug("kt3_find_plastic: mid_left=mid_left,mid_right=mid_right,mid_top=mid_top,mid_bottom=mid_bottom");
        }
        if (mid_top < 0) {
            //pmc_debug("XERROR " . ERR_FIND_TOP_SIDE_OF_STRIP . "-Cannot find the top side of strip");
            plastic.setError(ERR_FIND_TOP_SIDE_OF_STRIP);
            return plastic;
        }
        if (mid_bottom < 0) {
            //pmc_debug("XERROR " . ERR_FIND_BOTTOM_SIDE_OF_STRIP . "-Cannot find the bottom side of strip");
            plastic.setError(ERR_FIND_BOTTOM_SIDE_OF_STRIP);
            return plastic;
        }

        //
        // prepare references variables
        //
        float xlen        = (mid_bottom - mid_top) * 1840 / 565;
        mid_x       = (mid_right - mid_left) / 2 + mid_left;
        float mid_width   = (mid_right - mid_left) / 2;
        float mid_width8  = (mid_right - mid_left) / 8;
        mid_y       = (mid_bottom - mid_top) / 2 + mid_top;
        float mid_height8 = (mid_bottom - mid_top) / 8;

        //
        // find blue line #h1-h3
        //
        float h1_y     = mid_y - mid_height8;
        float h1_left  = kt3_find_blue_line_left(myBitmap, width, Math.round(h1_y));
        float h1_right = kt3_find_blue_line_right(myBitmap, width, Math.round(h1_y));

        float h2_y     = mid_y;
        float h2_left  = kt3_find_blue_line_left(myBitmap, width, Math.round(h2_y));
        float h2_right = kt3_find_blue_line_right(myBitmap, width, Math.round(h2_y));

        float h3_y     = mid_y + mid_height8;
        float h3_left  = kt3_find_blue_line_left(myBitmap, width, Math.round(h3_y));
        float h3_right = kt3_find_blue_line_right(myBitmap, width, Math.round(h3_y));

        //
        // find blue line #v1-v4
        //
        float v1_x      = mid_x - mid_width8;
        float v1_top    = kt3_find_blue_line_top(myBitmap, height, Math.round(v1_x));
        float v1_bottom = kt3_find_blue_line_bottom(myBitmap, height, Math.round(v1_x));

        float v2_x      = mid_x;
        float v2_top    = kt3_find_blue_line_top(myBitmap, height, (int)v2_x);
        float v2_bottom = kt3_find_blue_line_bottom(myBitmap, height, Math.round(v2_x));

        float v3_x      = mid_x + mid_width8;
        float v3_top    = kt3_find_blue_line_top(myBitmap, height, Math.round(v3_x));
        float v3_bottom = kt3_find_blue_line_bottom(myBitmap, height, Math.round(v3_x));

        float v4_x      = mid_right - xlen;
        float v4_top    = kt3_find_blue_line_top(myBitmap, height, Math.round(v4_x));
        float v4_bottom = kt3_find_blue_line_bottom(myBitmap, height, Math.round(v4_x));

        //
        // set h1-h3, v1-v4
        //
        plastic.set("h1_y", h1_y);
        plastic.set("h1_left", h1_left);
        plastic.set("h1_right", h1_right);
        plastic.set("h2_y", h2_y);
        plastic.set("h2_left", h2_left);
        plastic.set("h2_right", h2_right);
        plastic.set("h3_y", h3_y);
        plastic.set("h3_left", h3_left);
        plastic.set("h3_right", h3_right);
        plastic.set("v1_x", v1_x);
        plastic.set("v1_top", v1_top);
        plastic.set("v1_bottom", v1_bottom);
        plastic.set("v2_x", v2_x);
        plastic.set("v2_top", v2_top);
        plastic.set("v2_bottom", v2_bottom);
        plastic.set("v3_x", v3_x);
        plastic.set("v3_top", v3_top);
        plastic.set("v3_bottom", v3_bottom);
        plastic.set("v4_x", v4_x);
        plastic.set("v4_top", v4_top);
        plastic.set("v4_bottom", v4_bottom);

        // !!!!! The following code does not work!!!!!

        // //
        // // find end points
        // //
        // plastic_parm = plastic->toArray();
        // pLeftEnd     = kt3_find_blue_line_left2(source, width, plastic_parm);
        // pRightEnd    = kt3_find_blue_line_right2(source, width, plastic_parm);

        // pTopEnd    = kt3_find_blue_line_top2(source, height, plastic_parm);
        // pBottomEnd = kt3_find_blue_line_bottom2(source, height, plastic_parm);

        // // set end points
        // plastic->setPoint('plastic_left', point1);
        // plastic->setPoint('plastic_right', point2);
        // plastic->setPoint('plastic_top', point3);
        // plastic->setPoint('plastic_bottom', point4);

        return plastic;
    }

    /*
     * get the top point of the blue line
     */
    public float kt3_find_blue_line_top(Bitmap bitMapImage, int height, int x)
    {
        int topY = -1;
        int ok   = 0;
        for (int y = 0; y < height - 1; y += 1) {
            if (y == 703){
                Log.w(TAG, "");
            }
            boolean isPlastic = kt3_is_plastic(bitMapImage, x, y);

            if (!isPlastic) {
                ok   = 0;
                topY = -1;
                continue;
            }

            if (ok == 0) {
                topY = y; // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return topY;
    }

    /*
     * get the bottom point of the blue line
     */
    public float kt3_find_blue_line_bottom(Bitmap bitMapImage, float height, int x)
    {
        int bottomY = -1;
        int ok      = 0;
        for (int y = (int)height - 1; y > 0; y-=1) {
            if (y == 862){
                Log.w(TAG, "");
            }
            boolean isPlastic = kt3_is_plastic(bitMapImage, x, y);

            if (!isPlastic) {
                ok      = 0;
                bottomY = -1;
                continue;
            }

            if (ok == 0) {
                bottomY = y; // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return bottomY;
    }

    /*
     * get the start and end point of the blue line
     */
    public float kt3_find_blue_line_left(Bitmap bitMapImage, float width, int y)
    {
        int startX = -1;
        int ok     = 0;
        for (int x = 0; x < (int)width - 1; x+=1) {
            if (x == 263){
                Log.w(TAG, "");
            }
            boolean isPlastic = kt3_is_plastic(bitMapImage, x, y);

            if (!isPlastic) {
                ok     = 0;
                startX = -1;
                continue;
            }

            if (ok == 0) {
                startX = x; // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return startX;
    }

    /*
     * get the right point of the blue line
     */
    public float kt3_find_blue_line_right(Bitmap bitMapImage, float width, int y)
    {
        int endX = -1;
        int ok   = 0;
        for (int x = (int)width - 1; x > 0; x-=1) {
            boolean isPlastic = kt3_is_plastic(bitMapImage, x, y);

            if (!isPlastic) {
                ok   = 0;
                endX = -1;
                continue;
            }

            if (ok == 0) {
                endX = x; // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return endX;
    }

    public Map<String, Object> kt3_find_blue_line_top2(String filePath, float height, Map<String, Object> plastic_parm)
    {
        File imgFile = new File(filePath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int ok    = 0;
        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", -1);
        point.put("y", -1);

        // check h1
        int h1_y     = Integer.parseInt((String)plastic_parm.get("h1_y"));
        int h1_left  = Integer.parseInt((String)plastic_parm.get("h1_left"));
        int h1_right = Integer.parseInt((String)plastic_parm.get("h1_right"));
        int y1       = h1_left + (h1_right - h1_left) / 2;

        // check h3
        int h3_y     = Integer.parseInt((String)plastic_parm.get("h3_y"));
        int h3_left  = Integer.parseInt((String)plastic_parm.get("h3_left"));
        int h3_right = Integer.parseInt((String)plastic_parm.get("h3_right"));
        int y2       = h3_left + (h3_right - h3_left) / 2;

        for (int y = 0; y < (int)height - 1; y++) {
            int x          = h1_y + ((h3_y - h1_y) * (y - y1)) / (y2 - y1);
            point.put("x", x);

            boolean isPlastic = kt3_is_plastic(myBitmap, x, y);
            if (!isPlastic) {
                ok         = 0;
                point.put("y", -1);
                continue;
            }

            if (ok == 0) {
                point.put("y", y); // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return point;
    }

    public Map<String, Object> kt3_find_blue_line_bottom2(String filePath, float height, Map<String, Object> plastic_parm)
    {
        File imgFile = new File(filePath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        int ok    = 0;
        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", -1);
        point.put("y", -1);

        // check h1
        int h1_y     = Integer.parseInt((String)plastic_parm.get("h1_y"));
        int h1_left  = Integer.parseInt((String)plastic_parm.get("h1_left"));
        int h1_right = Integer.parseInt((String)plastic_parm.get("h1_right"));
        int y1       = h1_left + (h1_right - h1_left) / 2;

        // check h3
        int h3_y     = Integer.parseInt((String)plastic_parm.get("h3_y"));
        int h3_left  = Integer.parseInt((String)plastic_parm.get("h3_left"));
        int h3_right = Integer.parseInt((String)plastic_parm.get("h3_right"));
        int y2       = h3_left + (h3_right - h3_left) / 2;

        for (int y = (int) height - 1; y > 0; y--) {
            int x          = h1_y + ((h3_y - h1_y) * (y - y1)) / (y2 - y1);
            point.put("x", x);

            boolean isPlastic = kt3_is_plastic(myBitmap, x, y);
            if (!isPlastic) {
                ok         = 0;
                point.put("y", -1);;
                continue;
            }

            if (ok == 0) {
                point.put("y", y); // first time
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return point;
    }

    public Map<String, Object>  kt3_find_blue_line_left2(String filePath, float width, Map<String, Object> plastic_parm)
    {
        File imgFile = new File(filePath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int ok    = 0;
        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", -1);
        point.put("y", -1);

        // check v1
        int v1_x      = Integer.parseInt((String)plastic_parm.get("v1_x"));
        int v1_top    = Integer.parseInt((String)plastic_parm.get("v1_top"));
        int v1_bottom = Integer.parseInt((String)plastic_parm.get("v1_bottom"));
        int y1        = v1_top + (v1_bottom - v1_top) / 2;

        // check v3
        int v3_x      = Integer.parseInt((String)plastic_parm.get("v3_x"));
        int v3_top    = Integer.parseInt((String)plastic_parm.get("v3_top"));
        int v3_bottom = Integer.parseInt((String)plastic_parm.get("v3_bottom"));
        int y2        = v3_top + (v3_bottom - v3_top) / 2;

        for (int x = 0; x <(int) width - 1; x++) {
            int y          = y1 + ((y2 - y1) * (x - v1_x)) / (v3_x - v1_x);
            point.put("y", y);

            boolean isPlastic = kt3_is_plastic(myBitmap, x, y);
            if (!isPlastic) {
                ok         = 0;
                point.put("x", -1);
                continue;
            }

            if (ok == 0) {
                // first time
                point.put("x", x);
                point.put("y", y);
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return point;
    }

    public Map<String, Object>  kt3_find_blue_line_right2(String filePath, float width, Map<String, Object> plastic_parm)
    {
        File imgFile = new File(filePath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int ok    = 0;
        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", -1);
        point.put("y", -1);

        // check v1
        int v1_x      = Integer.parseInt((String)plastic_parm.get("v1_x"));
        int v1_top    = Integer.parseInt((String)plastic_parm.get("v1_top"));
        int v1_bottom = Integer.parseInt((String)plastic_parm.get("v1_bottom"));
        int y1        = v1_top + (v1_bottom - v1_top) / 2;

        // check v3
        int v3_x      = Integer.parseInt((String)plastic_parm.get("v3_x"));
        int v3_top    = Integer.parseInt((String)plastic_parm.get("v3_top"));
        int v3_bottom = Integer.parseInt((String)plastic_parm.get("v3_bottom"));
        int y2        = v3_top + (v3_bottom - v3_top) / 2;
        // pmc_debug("kt3_find_blue_line_right2:x1=v1_x,y1=y1,x2=v3_x,y2=y2");

        int end_x   = -1;
        int point_x = -1;
        int point_y = -1;
        int fail    = 0;
        ok      = 0;
        for (int x = (int)width - 1; x > 0; x--) {
            int y          = y1 + ((y2 - y1) * (x - v1_x)) / (v3_x - v1_x);
            point.put("y", y);

            boolean isPlastic = kt3_is_plastic(myBitmap, x, y);
            if (!isPlastic) {
                ok         = 0;
                point.put("x", -1);
                continue;
            }

            if (ok == 0) {
                // first time
                point.put("x", x);
                point.put("y", y);
            }

            if (++ok > THRESHOLD) {
                break; // found more than 'THRESHOLD'
            }
        }

        return point;
    }

    public boolean kt3_is_plastic(Bitmap bitMapImage, int x, int y)
    {
        int[] rgba = colorsForPoint(bitMapImage, x, y);
        double[] lchArray = RGB2Lch(rgba[0], rgba[1], rgba[2]);
        double cie_l = lchArray[0];
        double cie_c = lchArray[1];
        double cie_h = lchArray[2];
        return cie_c > 9.2 && cie_h > Math.PI && cie_h < 1.7 * Math.PI;
    }

    /*
    * get the end point given length from the starting point
     */
    public Map<String, Object>  kt3_get_end_point(String filePath, float len0, Map<String, Object> plastic_parm)
    {
        // check left
        float x1 = Float.parseFloat((String)plastic_parm.get("plastic_left_x"));
        float y1 = Float.parseFloat((String)plastic_parm.get("plastic_left_y"));
        // check right
        float x2 = Float.parseFloat((String)plastic_parm.get("plastic_right_x"));
        float y2 = Float.parseFloat((String)plastic_parm.get("plastic_right_y"));

        double len1 = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        int yy1  = Math.round(y1);
        int yy2  = Math.round(y2);

        int x0 = (int)(x1 + (x2 - x1) * len0);
        int y0 = (int)(y1 + (y2 - y1) * len0);

        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", x0);
        point.put("y", y0);
        return point;
    }

    /*
     * get the end point given length from the starting point
     */
    public Map<String, Object> kt3_get_end_point_vertical(String filePath, float len0, Map<String, Object> plastic_parm)
    {
        // check top
        float x1 = Float.parseFloat((String)plastic_parm.get("plastic_top_x"));
        float y1 = Float.parseFloat((String)plastic_parm.get("plastic_top_y"));
        // check bottom
        float x2 = Float.parseFloat((String)plastic_parm.get("plastic_bottom_x"));
        float y2 = Float.parseFloat((String)plastic_parm.get("plastic_bottom_y"));

        double len1 = sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        int yy1  = Math.round(y1);
        int yy2  = Math.round(y2);

        int x0 = (int)(x1 + (x2 - x1) * len0);
        int y0 = (int)(y1 + (y2 - y1) * len0);

        Map<String, Object> point = new HashMap<String, Object>();
        point.put("x", x0);
        point.put("y", y0);
        return point;
    }

    //    public int kt3_calculate_horizontal(String filePath, Map<String, Object> raw_data, Map<String, Object> plastic_parm)
//    {
//        //
//        //      hole left
//        //
//        int len0        = temp2_hole_left_x;
//        Map<String, Object> point       = kt3_get_end_point(filePath, len0, plastic_parm);
//        int x           = Integer.parseInt((String)point.get("point_x");
//        int y           = Integer.parseInt((String) point.get("point_y");
//        float avg         = kt3_calculate_neighbor(filePath, x, y);
//        int xavg        = Math.round(avg);
//        int hole_left_x = Math.round(x);
//        int hole_left_y = Math.round(y);
//        int hole_left   = kt3_is_hole2(filePath, x, y);
//
//        //
//        //      hole right
//        //
//        len0         = temp2_hole_right_x;
//        point        = kt3_get_end_point(filePath, len0, plastic_parm);
//        x           = Integer.parseInt((String)point.get("x");
//        y           = Integer.parseInt((String) point.get("y");
//        avg          = kt3_calculate_neighbor(filePath, x, y);
//        xavg         = Math.round(avg);
//        int hole_right_x = Math.round(x);
//        int hole_right_y = Math.round(y);
//        int hole_right   = kt3_is_hole2(filePath, x, y);
//
//        // pmc_debug("kt3_extract_plastic:hole_right: hole_right_x=hole_right_x,hole_right_y=hole_right_y,hole_right=hole_right");
//
//        if (hole_left == 0 && hole_right == 1) {
//            // rotation = 0
//            len0  = temp2_background_x;
//            point = kt3_get_end_point(filePath, len0, plastic_parm);
//            x           = Integer.parseInt((String)point.get("x");
//            y           = Integer.parseInt((String) point.get("y");
//            // check background
//            avg                              = kt3_calculate_neighbor(filePath, x, y);
//            int rgb                              = imagecolorat(filePath, background_x, background_y);
//            int[] colors_row                       = imagecolorsforindex(filePath, rgb);
//            raw_data.put("raw_background_red", colors_row[0]);
//            raw_data['raw_background_green'] = colors_row[1];
//            raw_data['raw_background_blue']  = colors_row[2];
//            raw_data['raw_background_x']     = pmc_rounding(x, 0);
//            raw_data['raw_background_y']     = pmc_rounding(y, 0);
//            raw_data['raw_background_avg']   = pmc_rounding(avg, 1);
//            // check signal
//            len0                         = temp2_signal_x;
//            point                        = kt3_get_end_point(source, len0, plastic_parm);
//            x                            = point['x'];
//            y                            = point['y'];
//            avg                          = kt3_calculate_neighbor(source, x, y);
//            rgb                          = imagecolorat(source, x, y);
//            colors_row                   = imagecolorsforindex(source, rgb);
//            raw_data['raw_signal_red']   = colors_row['red'];
//            raw_data['raw_signal_green'] = colors_row['green'];
//            raw_data['raw_signal_blue']  = colors_row['blue'];
//            raw_data['raw_signal_x']     = pmc_rounding(x, 0);
//            raw_data['raw_signal_y']     = pmc_rounding(y, 0);
//            raw_data['raw_signal_avg']   = pmc_rounding(avg, 1);
//            // check ph
//            len0                     = temp2_ph_x;
//            point                    = kt3_get_end_point(source, len0, plastic_parm);
//            x                        = point['x'];
//            y                        = point['y'];
//            ph_avg                   = kt3_calculate_ph(source, x, y);
//            rgb                      = imagecolorat(source, ph_x, ph_y);
//            colors_row               = imagecolorsforindex(source, rgb);
//            raw_data['raw_ph_red']   = colors_row['red'];
//            raw_data['raw_ph_green'] = colors_row['green'];
//            raw_data['raw_ph_blue']  = colors_row['blue'];
//            raw_data['raw_ph_x']     = pmc_rounding(x, 0);
//            raw_data['raw_ph_y']     = pmc_rounding(y, 0);
//            raw_data['raw_ph_avg']   = pmc_rounding(avg, 1);
//            raw_data['raw_rotate']   = 0;
//            // pmc_debug("calculate_horizontal:rotation=0");
//            return (0);
//        }
//
//        if (hole_left == 1 && hole_right == 0) {
//            // rotation = 0
//            len0  = (1 - temp2_background_x);
//            point = kt3_get_end_point(source, len0, plastic_parm);
//            x     = point['x'];
//            y     = point['y'];
//            // check background
//            avg                              = kt3_calculate_neighbor(source, x, y);
//            rgb                              = imagecolorat(source, background_x, background_y);
//            colors_row                       = imagecolorsforindex(source, rgb);
//            raw_data['raw_background_red']   = colors_row['red'];
//            raw_data['raw_background_green'] = colors_row['green'];
//            raw_data['raw_background_blue']  = colors_row['blue'];
//            raw_data['raw_background_x']     = pmc_rounding(x, 0);
//            raw_data['raw_background_y']     = pmc_rounding(y, 0);
//            raw_data['raw_background_avg']   = pmc_rounding(avg, 1);
//            // check signal
//            len0                         = 1 - temp2_signal_x;
//            point                        = kt3_get_end_point(source, len0, plastic_parm);
//            x                            = point['x'];
//            y                            = point['y'];
//            avg                          = kt3_calculate_neighbor(source, x, y);
//            rgb                          = imagecolorat(source, x, y);
//            colors_row                   = imagecolorsforindex(source, rgb);
//            raw_data['raw_signal_red']   = colors_row['red'];
//            raw_data['raw_signal_green'] = colors_row['green'];
//            raw_data['raw_signal_blue']  = colors_row['blue'];
//            raw_data['raw_signal_x']     = pmc_rounding(x, 0);
//            raw_data['raw_signal_y']     = pmc_rounding(y, 0);
//            raw_data['raw_signal_avg']   = pmc_rounding(avg, 1);
//            // check ph
//            len0                     = 1 - temp2_ph_x;
//            point                    = kt3_get_end_point(source, len0, plastic_parm);
//            x                        = point['x'];
//            y                        = point['y'];
//            ph_avg                   = kt3_calculate_ph(source, x, y);
//            rgb                      = imagecolorat(source, ph_x, ph_y);
//            colors_row               = imagecolorsforindex(source, rgb);
//            raw_data['raw_ph_red']   = colors_row['red'];
//            raw_data['raw_ph_green'] = colors_row['green'];
//            raw_data['raw_ph_blue']  = colors_row['blue'];
//            raw_data['raw_ph_x']     = pmc_rounding(x, 0);
//            raw_data['raw_ph_y']     = pmc_rounding(y, 0);
//            raw_data['raw_ph_avg']   = pmc_rounding(avg, 1);
//            rotate                   = 180;
//            raw_data['raw_rotate']   = 180;
//            // pmc_debug("calculate_horizontal:rotation=180");
//            return (rotate);
//        }
//        rotate                 = -1;
//        raw_data['raw_rotate'] = -1;
//        pmc_debug("calculate_horizontal:rotation=-1");
//        return (rotate);
//    }
//
//    public int kt3_calculate_vertical(String filePath, Map<String, Object> raw_data, Map<String, Object> plastic_parm)
//    {
//        // pmc_debug("kt3_calculate_vertical called");
//
//        //
//        //      hole bottom
//        //
//        int len0          = temp2_hole_left_x;
//        Map<String, Object> point         = kt3_get_end_point_vertical(filePath, len0);
//        int x             = Integer.parseInt((String)point.get("x");
//        int y             = Integer.parseInt((String)point.get("y");
//        avg           = kt3_calculate_neighbor(source, x, y);
//        xavg          = pmc_rounding(avg, 1);
//        hole_bottom_x = pmc_rounding(x, 0);
//        hole_bottom_y = pmc_rounding(y, 0);
//        hole_bottom   = kt3_is_hole2(source, x, y);
//
//        // pmc_debug("kt3_extract_plastic:hole_bottom: x=hole_bottom_x,y=hole_bottom_y,hole_bottom=hole_bottom");
//
//        //
//        //      hole top
//        //
//        len0       = temp2_hole_right_x;
//        point      = kt3_get_end_point_vertical(source, len0);
//        x          = point['x'];
//        y          = point['y'];
//        avg        = kt3_calculate_neighbor(source, x, y);
//        xavg       = pmc_rounding(avg, 1);
//        hole_top_x = pmc_rounding(x, 0);
//        hole_top_y = pmc_rounding(y, 0);
//        hole_top   = kt3_is_hole2(source, x, y);
//
//        // pmc_debug("kt3_extract_plastic:hole_top: hole_top_x=hole_top_x,hole_top_y=hole_top_y,hole_top=hole_top");
//
//        if (hole_top == 0 && hole_bottom == 1) {
//            // rotation = 90
//            len0  = temp2_background_x;
//            point = kt3_get_end_point_vertical(source, len0);
//            x     = point['x'];
//            y     = point['y'];
//            // check background
//            avg                              = kt3_calculate_neighbor(source, x, y);
//            rgb                              = imagecolorat(source, background_x, background_y);
//            colors_row                       = imagecolorsforindex(source, rgb);
//            raw_data['raw_background_red']   = colors_row['red'];
//            raw_data['raw_background_green'] = colors_row['green'];
//            raw_data['raw_background_blue']  = colors_row['blue'];
//            raw_data['raw_background_x']     = pmc_rounding(x, 0);
//            raw_data['raw_background_y']     = pmc_rounding(y, 0);
//            raw_data['raw_background_avg']   = pmc_rounding(avg, 1);
//            // check signal
//            len0                         = temp2_signal_x;
//            point                        = kt3_get_end_point_vertical(source, len0);
//            x                            = point['point_x'];
//            y                            = point['point_y'];
//            avg                          = kt3_calculate_neighbor(source, x, y);
//            rgb                          = imagecolorat(source, x, y);
//            colors_row                   = imagecolorsforindex(source, rgb);
//            raw_data['raw_signal_red']   = colors_row['red'];
//            raw_data['raw_signal_green'] = colors_row['green'];
//            raw_data['raw_signal_blue']  = colors_row['blue'];
//            raw_data['raw_signal_x']     = pmc_rounding(x, 0);
//            raw_data['raw_signal_y']     = pmc_rounding(y, 0);
//            raw_data['raw_signal_avg']   = pmc_rounding(avg, 1);
//            // check ph
//            len0                     = temp2_ph_x;
//            point                    = kt3_get_end_point_vertical(source, len0);
//            x                        = point['point_x'];
//            y                        = point['point_y'];
//            ph_avg                   = kt3_calculate_ph(source, x, y);
//            rgb                      = imagecolorat(source, ph_x, ph_y);
//            colors_row               = imagecolorsforindex(source, rgb);
//            raw_data['raw_ph_red']   = colors_row['red'];
//            raw_data['raw_ph_green'] = colors_row['green'];
//            raw_data['raw_ph_blue']  = colors_row['blue'];
//            raw_data['raw_ph_x']     = pmc_rounding(x, 0);
//            raw_data['raw_ph_y']     = pmc_rounding(y, 0);
//            raw_data['raw_ph_avg']   = pmc_rounding(avg, 1);
//            raw_data['raw_rotate']   = 90;
//            // pmc_debug("calculate_vertical:rotation=90");
//            return (0);
//        }
//
//        if (hole_top == 1 && hole_bottom == 0) {
//            // rotation = 270
//            len0  = (1 - temp2_background_x);
//            point = kt3_get_end_point_vertical(source, len0);
//            x     = point['point_x'];
//            y     = point['point_y'];
//            // check background
//            avg                              = kt3_calculate_neighbor(source, x, y);
//            rgb                              = imagecolorat(source, background_x, background_y);
//            colors_row                       = imagecolorsforindex(source, rgb);
//            raw_data['raw_background_red']   = colors_row['red'];
//            raw_data['raw_background_green'] = colors_row['green'];
//            raw_data['raw_background_blue']  = colors_row['blue'];
//            raw_data['raw_background_x']     = pmc_rounding(x, 0);
//            raw_data['raw_background_y']     = pmc_rounding(y, 0);
//            raw_data['raw_background_avg']   = pmc_rounding(avg, 1);
//            // check signal
//            len0                         = 1 - temp2_signal_x;
//            point                        = kt3_get_end_point_vertical(source, len0);
//            x                            = point['point_x'];
//            y                            = point['point_y'];
//            avg                          = kt3_calculate_neighbor(source, x, y);
//            rgb                          = imagecolorat(source, x, y);
//            colors_row                   = imagecolorsforindex(source, rgb);
//            raw_data['raw_signal_red']   = colors_row['red'];
//            raw_data['raw_signal_green'] = colors_row['green'];
//            raw_data['raw_signal_blue']  = colors_row['blue'];
//            raw_data['raw_signal_x']     = pmc_rounding(x, 0);
//            raw_data['raw_signal_y']     = pmc_rounding(y, 0);
//            raw_data['raw_signal_avg']   = pmc_rounding(avg, 1);
//            // check ph
//            len0                     = 1 - temp2_ph_x;
//            point                    = kt3_get_end_point_vertical(source, len0);
//            x                        = point['point_x'];
//            y                        = point['point_y'];
//            ph_avg                   = kt3_calculate_ph(source, x, y);
//            rgb                      = imagecolorat(source, ph_x, ph_y);
//            colors_row               = imagecolorsforindex(source, rgb);
//            raw_data['raw_ph_red']   = colors_row['red'];
//            raw_data['raw_ph_green'] = colors_row['green'];
//            raw_data['raw_ph_blue']  = colors_row['blue'];
//            raw_data['raw_ph_x']     = pmc_rounding(x, 0);
//            raw_data['raw_ph_y']     = pmc_rounding(y, 0);
//            raw_data['raw_ph_avg']   = pmc_rounding(avg, 1);
//            rotate                   = 270;
//            raw_data['raw_rotate']   = 270;
//            // pmc_debug("calculate_vertical:rotation=180");
//            return (rotate);
//        }
//        rotate                 = -1;
//        raw_data['raw_rotate'] = -1;
//        pmc_debug("calculate_vertical:rotation=-1");
//        return (rotate);
//    }
    public int imagecolorat(Bitmap myBitmap, int x, int y){
        int p = myBitmap.getPixel(x, y);
        return p;
    }
    public int[] imagecolorsforindex(Bitmap myBitmap, int p){
        int red = Color.red(p);
        int green = Color.green(p);
        int blue = Color.blue(p);
        int[] colorIndes = new int[3];
        colorIndes[0] = red;
        colorIndes[1] = green;
        colorIndes[2] = blue;
        return colorIndes;
    }
    public Map<String, Object> kt3_get_result(Bitmap myBitmap, float width, float height)
    {
        Map<String, Object> dec_row = new HashMap<String, Object>();
        float x1_min = 0;
        float x1_max = width / 2;

        //
        //      Locations for all interesting points
        //
        int plastic1_x = 150;
        int plastic1_y = 100;
        int plastic2_x = 151;
        int plastic2_y = 101;
/* old
background_x = 566;
background_y = 150;
glucose_x    = 455;
glucose_y    = 150;
 */
        int background_x = 150;
        int background_y = GLOBAL_BACKGROUND_X;
// change from 770 to GLOBAL_SIGNAL_X
        int glucose_x  = 150;
        int glucose_y  = GLOBAL_SIGNAL_X;
        int ph_x       = 150;
        int ph_y       = 397;
        int qr_white_x = 150;
        int qr_white_y = 845;
        int calib1_x   = 80;
        int calib1_y   = 306;
        int calib2_x   = 155;
        int calib2_y   = calib1_x;
        int calib3_x   = 234;
        int calib3_y   = calib1_x;
        //
        //      Find plastic 1
        //
        int x   = plastic1_x;
        int y   = plastic1_y;

        int p = myBitmap.getPixel(x, y);
        int red = Color.red(p);
        int green = Color.green(p);
        int blue = Color.blue(p);
        float value  = red - (green + blue) / 2;

        int rgb = imagecolorat(myBitmap, x, y);
        //
        int[] colors_row = imagecolorsforindex(myBitmap, rgb);
//print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        value = blue - (green + red) / 2;

        dec_row.put("dec_plastic1_x", x);
        dec_row.put("dec_plastic1_y", y);
        dec_row.put("dec_plastic1_r", red);
        dec_row.put("dec_plastic1_g", green);
        dec_row.put("dec_plastic1_b", blue);
        dec_row.put("dec_plastic1_value", Math.round(value));

        //
        //      Find plastic 2
        //
        x   = plastic2_x;
        y   = plastic2_y;
        rgb = imagecolorat(myBitmap, x, y);
        //
        colors_row = imagecolorsforindex(myBitmap, rgb);
//print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        value = blue - (green + red) / 2;

        dec_row.put("dec_plastic2_x", x);
        dec_row.put("dec_plastic2_y", y);
        dec_row.put("dec_plastic2_r", red);
        dec_row.put("dec_plastic2_g", green);
        dec_row.put("dec_plastic2_b", blue);
        dec_row.put("dec_plastic2_value", Math.round(value));

        //
        //      Find strip background
        //
        x          = background_x;
        y          = background_y;
        colors_row = colorsForPoint(myBitmap, x, y);
//print_r(colors_row);
        //pmc_debug("color_row");
        red       = colors_row[0];
        green     = colors_row[1];
        blue      = colors_row[2];
        value     = red - (green + blue) / 2;

        if_lib ifLIb = new if_lib();
        float avg_value = ifLIb.lf_find_average(myBitmap, x, y);
        if (VERBOSE > 1) {
            //pmc_debug("background_value=avg_value,value=value");
        }

        value = avg_value;

        if (VERBOSE > 1) {
            // pmc_debug("background value=value");
        }

//        if(value < 0)
        //                value = 0;
        dec_row.put("dec_background_x", x);
        dec_row.put("dec_background_y", y);
        dec_row.put("dec_background_r", red);
        dec_row.put("dec_background_g", green);
        dec_row.put("dec_background_b", blue);
        dec_row.put("dec_background_value", (int)value);

        //
        //      Find strip glucose
        //
        x          = glucose_x;
        y          = glucose_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red       = colors_row[0];
        green     = colors_row[1];
        blue      = colors_row[2];
        value     = red - (green + blue) / 2;
        avg_value = ifLIb.lf_find_red(myBitmap, x, y);
        if (VERBOSE > 1) {
            //pmc_debug("glucose_value=avg_value,value=value");
        }

        value = avg_value;

        dec_row.put("dec_glucose_x", x);
        dec_row.put("dec_glucose_y", y);
        dec_row.put("dec_glucose_r", red);
        dec_row.put("dec_glucose_g", green);
        dec_row.put("dec_glucose_b", blue);
        dec_row.put("dec_glucose_value", (int)value);

        //
        //      Find strip ph
        //
        x          = ph_x;
        y          = ph_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        value = green - (red + blue) / 2;

        dec_row.put("dec_ph_x", ph_x);
        dec_row.put("dec_ph_y", ph_y);
        dec_row.put("dec_ph_x", x);
        dec_row.put("dec_ph_y", y);
        dec_row.put("dec_ph_r", red);
        dec_row.put("dec_ph_g", green);
        dec_row.put("dec_ph_b", blue);
        dec_row.put("dec_ph_value", (int)value);

        //
        //      Find QR white space
        //
        x          = qr_white_x;
        y          = qr_white_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        value = red - (green + blue) / 2;

        dec_row.put("qr_white_x", x);
        dec_row.put("qr_white_y", y);
        dec_row.put("qr_white_red", red);
        dec_row.put("qr_white_green", green);
        dec_row.put("qr_white_blue", blue);
        dec_row.put("qr_white_value", (int)value);

        //
        //      Find calib #1
        //
        x          = calib1_x;
        y          = calib1_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        float r1    = red - (green + blue) / 2;

        dec_row.put("dec_calib1_x", x);
        dec_row.put("dec_calib1_y", y);
        dec_row.put("dec_calib1_r", red);
        dec_row.put("dec_calib1_g", green);
        dec_row.put("dec_calib1_b", blue);
        dec_row.put("dec_calib1_value", (int)r1);

        //
        //      Find calib #2
        //
        x          = calib2_x;
        y          = calib2_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        r1    = red - (green + blue) / 2;

        dec_row.put("dec_calib2_x", x);
        dec_row.put("dec_calib2_y", y);
        dec_row.put("dec_calib2_r", red);
        dec_row.put("dec_calib2_g", green);
        dec_row.put("dec_calib2_b", blue);
        dec_row.put("dec_calib2_value", (int)r1);
        //
        //      Find calib #3
        //
        x          = calib3_x;
        y          = calib3_y;
        colors_row = colorsForPoint(myBitmap, x, y);
        //print_r(colors_row);
        //pmc_debug("color_row");
        red   = colors_row[0];
        green = colors_row[1];
        blue  = colors_row[2];
        value = red - (green + blue) / 2;

        dec_row.put("dec_calib3_x", x);
        dec_row.put("dec_calib3_y", y);
        dec_row.put("dec_calib3_r", red);
        dec_row.put("dec_calib3_g", green);
        dec_row.put("dec_calib3_b", blue);
        dec_row.put("dec_calib3_value", (int)value);
        return (dec_row);
    }
    /*
     * Check Quality of Image
     */
    public int kt3_check_quality( Map<String, Object> dec_row)
    {
        // qr1
        String category = "lf";
        String type     = "range-low-qr1";
        String value = dbother_getcomment1(category, type);

        int low = 0;
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-qr1";
        value = dbother_getcomment1(category, type);

        int high = 0;
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int calib1_low  = low;
        int calib1_high = high;
        // qr2
        category = "lf";
        type     = "range-low-qr2";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-qr2";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int calib2_low  = low;
        int calib2_high = high;
        // qr3
        category = "lf";
        type     = "range-low-qr3";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-qr3";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int calib3_low  = low;
        int calib3_high = high;
        // qr4
        category = "lf";
        type     = "range-low-qr4";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-qr4";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        // background                        category = "lf";
        type  = "range-low-background";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-background";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int background_low  = low;
        int background_high = high;
        // ph
        category = "lf";
        type     = "range-low-ph";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-ph";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int ph_low  = low;
        int ph_high = high;
        // glucose
        category = "lf";
        type     = "range-low-glucose";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-glucose";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int glucose_low  = low;
        int glucose_high = high;
        // plastic 1
        category = "lf";
        type     = "range-low-qr1";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-plastic1";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int plastic1_low  = low;
        int plastic1_high = high;
        //
        //      plastic 2
        //
        category = "lf";
        type     = "range-low-plastic2";
        value    = dbother_getcomment1(category, type);
        if (value == "") {
            low = -511;
        } else {
            low = Integer.parseInt(value);
        }
        type  = "range-high-plastic2";
        value = dbother_getcomment1(category, type);
        if (value == "") {
            high = 512;
        } else {
            high = Integer.parseInt(value);
        }
        int plastic2_low  = low;
        int plastic2_high = high;
        // if (VERBOSE > 1) {
        //     pmc_debug("calib1 low=calib1_low, high=calib1_high");
        //     pmc_debug("calib2 low=calib2_low, high=calib2_high");
        //     pmc_debug("calib3 low=calib3_low, high=calib3_high");
        //     pmc_debug("background low=background_low, high=background_high");
        //     pmc_debug("ph low=ph_low, high=ph_high");
        //     pmc_debug("glucose low=glucose_low, high=glucose_high");
        //     pmc_debug("plastic1 low=plastic1_low, high=plastic1_high");
        //     pmc_debug("plastic2 low=plastic2_low, high=plastic2_high");
        // }
        //
        //      check QR1
        //

        int status = 0;

        int calib1 = 0; // dec_row['dec_calib1_Value'];

        if (calib1 < calib1_low) {
            // if (VERBOSE > 0) {
            //     pmc_debug("Error: calib1=calib1,calib1_low=calib1_low");
            // }
            // xerror 201 - Calib #1 value too low
            status = 201;
            return status;
        }
        if (calib1 > calib1_high) {
            // xerror 202 - Calib #1 value too high
            status = 202;
            if (VERBOSE > 0) {
                //pmc_debug("Error: kt3_check_quality: calib1=calib1,calib1_low=calib1_low,calib1_high=calib1_high");
            }
            return status;
        }
        //
        //      check calib2
        //
        int calib2 = (Integer) dec_row.get("dec_calib2_value");

        if (calib2 < calib2_low) {
            if (VERBOSE > 0) {
                //pmc_debug("Error: calib2=calib2,calib2_low=calib2_low");
            }
            // xerror 203 - Calib #2 value too low
            status = 203;
            return (status);
        }

        if (calib2 > calib2_high) {
            // xerror 204 - Calib #2 value too high
            status = 204;
            if (VERBOSE > 0) {
                //pmc_debug("Error: kt3_check_quality: calib2=calib2,calib2_low=calib2_low,calib2_high=calib2_high");
            }
            return (status);
        }
        //
        //      check calib3
        //
        int calib3 = (Integer)dec_row.get("dec_calib3_value");

        if (calib3 < calib3_low) {
            if (VERBOSE > 0) {
                //pmc_debug("Error: calib3=calib3,calib3_low=calib3_low");
            }
            // xerror 205 - Calib #3 value too low
            status = 205;
            return (status);
        }
        if (calib3 > calib3_high) {
            if (VERBOSE > 0) {
                //pmc_debug("Error: calib3=calib3,calib3_high=calib3_high");
            }
            // xerror 206 - Calib #3 value too low
            status = 206;
            return (status);
        }
        //
        //      check Background
        //
        int background = (Integer)dec_row.get("dec_background_value");

        if (background < background_low) {
            if (VERBOSE > 0) {
                //pmc_debug("background_low=background_low,background=background");
            }
            // xerror 213 - Background value too low
            status = 213;
            return (status);
        }
        if (background > background_high) {
            // xerror 214 - Background value too high
            status = 214;
            return (status);
        }
        //
        //      check ph
        //
        int ph = (Integer)dec_row.get("dec_ph_value");

        if (ph < ph_low) {
            // xerror 215 - PH value too low
            status = 215;
            return (status);
        }
        if (ph > ph_high) {
            // xerror 216 - PH value too high
            status = 216;
            return (status);
        }
        //
        //      check Glucose
        //
        int glucose = (Integer)dec_row.get("dec_glucose_value");

        if (glucose < glucose_low) {
            // xerror 217 - Glucose value too low
            status = 217;
            return (status);
        }
        if (glucose > glucose_high) {
            // xerror 218 - Glucose value too low
            status = 218;
            return (status);
        }
        //
        //      check Plastic1
        //
        int plastic1 = (Integer)dec_row.get("dec_plastic1_value");

        if (plastic1 < plastic1_low) {
            // xerror 219 - Plastic #1 value too low
            status = 219;
            return (status);
        }
        if (plastic1 > plastic1_high) {
            // xerror 220 - Plastic #1 value too high
            status = 220;
            return (status);
        }
        //
        //      check Plastic2
        //
        int plastic2 = (Integer)dec_row.get("dec_plastic2_value");

        if (plastic2 < plastic2_low) {
            // xerror 221 - Plastic #2 value too low
            status = 221;
            return (status);
        }
        if (plastic2 > plastic2_high) {
            // xerror 222 - Plastic #2 value too high
            status = 222;
            return (status);
        }
        status = 0;
        return (status);
    }

    public Map<String, Object> kt3_update_record(Map<String, Object> dec_row, Map<String, Object> raw_data, Map<String, Object> plastic_parm)
    {
        String lf_result           = "invalid";
        int lf_glucose_value       = -1;
        int lf_ph                  = 0;
        int lf_color_value         = 0;
        int lf_blood_glucose_value = 0;

        int lf_status = (Integer) dec_row.get("dec_status");
        //pmc_debug("kt3_update_record: lf_status=lf_status");
        if (lf_status != 0) {
            //pmc_debug("kt3_update_record: invalid result");
            lf_result              = "invalid";
            lf_glucose_value       = -1;
            lf_ph                  = 0;
            lf_color_value         = 0;
            lf_blood_glucose_value = 0;
        } else {
            lf_ph = (Integer) dec_row.get("dec_ph_value");

            int xx1 = (Integer) dec_row.get("dec_glucose_value");
            int xx2 = (Integer) dec_row.get("dec_background_value");
            lf_color_value = (Integer) dec_row.get("dec_glucose_value") - (Integer) dec_row.get("dec_background_value");

            //pmc_debug("XXX foreground=xx1,background=xx2,color=lf_color_value");

            //pmc_debug("XXX 2,color=lf_color_value");

            if (lf_color_value < 0) {
                lf_color_value = 0;
            }
            //
            //      Convert color to glucose
            //

            if_lib ifLib = new if_lib();
            lf_glucose_value = (int)ifLib.lf_color_to_glucose(lf_color_value);
            //pmc_debug("XXXYY glucose_value=lf_glucose_value,color=lf_color_value");

            if (lf_glucose_value < 0) {
                //pmc_debug("kt3_update_record:lf_glucose_value=lf_glucose_value");
                lf_glucose_value = 0;
            }
            //
            //      Convert salivary to blood glucose
            //
            lf_blood_glucose_value = Math.round(ifLib.lf_glucose_to_blood(lf_glucose_value));

            //
            //      Convert Glucose to Result
            //
            lf_result = ifLib.lf_glucose_to_result(lf_glucose_value);
            if (lf_status != 0) {
                // invalid data
                lf_result = "invalid";
            }
        }

        String lf_result_info = kt3_get_result_info(dec_row, raw_data, plastic_parm);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("lf_ph", lf_ph);
        result.put("lf_color_value", lf_color_value);
        result.put("lf_glucose_value", lf_glucose_value);
        result.put("lf_blood_glucose_value", lf_blood_glucose_value);
        result.put("lf_result", lf_result);
        result.put("lf_status", lf_status);
        result.put("lf_result_info", lf_result_info);
        return result;
    }

    //
//      for automatic mode
//      get result_info given x,y for glucose and background spots
//
    public String kt3_get_result_info(Map<String, Object> dec_row, Map<String, Object> raw_data, Map<String, Object>  plastic_parm)
    {

        //print_r(raw_data);;
        //pmc_debug("kt3_get_result_info:raw_data");

        //
        //      calib 1
        //
        String info = "";
        info += Math.round((Float) plastic_parm.get("h1_left"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h1_right"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v1_top"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v1_bottom"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h1_y"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v1_x"));
        info += "~";
        String info0 = info;
//        pmc_debug("info0=info0");
        //
        //      calib 2
        //
        info = "";
        info += Math.round((Float) plastic_parm.get("h2_left"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h2_right"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v2_top"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v2_bottom"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h2_y"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v2_x"));
        info += "~";
        String info1 = info;
//        pmc_debug("info1=info1");
        //
        //      calib 3
        //
        info = "";
        info += Math.round((Float) plastic_parm.get("h3_left"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h3_right"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v3_top"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v3_bottom"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("h3_y"));
        info += "~";
        info += Math.round((Float) plastic_parm.get("v3_x"));
        info += "~";
        String info2 = info;
//        pmc_debug("info2=info2");
        //
        //      calculate new_glucose_x
        //
        int x1     = (Integer) dec_row.get("dec_glucose_x");
        int x2     =  (Integer) raw_data.get("raw_src_x");
        int x3     =  (Integer) raw_data.get("raw_src_w");
        int x4            = x1 * x3;
        int new_glucose_x = x4 / 1200 + x2;
//
//        pmc_debug("kt3_get_result_info:new_glucose_x=new_glucose_x,glucose_x=x1,src_x=x2,src_w=x3");
//        pmc_debug("rotate=rotate");

        //
        //      calculate new_glucose_y
        //
        x1 =  (Integer) dec_row.get("dec_glucose_y");
        x2 =  (Integer)raw_data.get("raw_src_y");
        x3 =  (Integer)raw_data.get("raw_src_h");
        x4            = x1 * x3;
        int new_glucose_y = x4 / 300 + x2;

        //pmc_debug("kt3_get_result_info:new_glucose_y=new_glucose_y,dec_glucose_y=x1");

        //
        //      calculate new_background_x
        //
        x1 = (Integer)dec_row.get("dec_background_x");
        x2 = (Integer)raw_data.get("raw_src_x");
        x3 = (Integer)raw_data.get("raw_src_w");
        x4               = x1 * x3;
        int new_background_x = x4 / 1200 + x2;

        //pmc_debug("kt3_get_result_info:new_background_x=new_background_x");

        //
        //      calculate new_background_y
        //
        x1 = (Integer)dec_row.get("dec_background_y");
        x2 = (Integer)raw_data.get("raw_src_y");
        x3 = (Integer)raw_data.get("raw_src_h");

        x4               = x1 * x3;
        int new_background_y = x4 / 300 + x2;

        //pmc_debug("kt3_get_result_info:new_background_y=new_background_y,dec_background_y=x1");

        //
        //xrow = explode("^", result_info);
        String[] xrow = {"_", "-", "-", "-", "-", "-"};

        //
        //      0 = calib1
        //      1 = calib2
        //      2 = calib3
        //      3 = ph
        //      4 = Manual background
        //      5 = Manual Glucose
        //      6 = Automatic background
        //      7 = Automatic Glucose
        //
        //      info3 - ph
        info = "";
        if (raw_data.get("raw_ph_x") != null)
            info += (Integer)raw_data.get("raw_ph_x");
        info += "~";
        if (raw_data.get("raw_ph_y") != null)
            info += (Integer)raw_data.get("raw_ph_y");
        info += "~";
        if (raw_data.get("raw_ph_red") != null)
            info += (Integer)raw_data.get("raw_ph_red");
        info += "~";
        if (raw_data.get("raw_ph_green") != null)
            info += (Integer)raw_data.get("raw_ph_green");
        info += "~";
        if (raw_data.get("raw_ph_blue") != null)
            info += (Integer)raw_data.get("raw_ph_blue");
        info += "~";
        if (raw_data.get("raw_ph_avg") != null)
            info += (Integer)raw_data.get("raw_ph_avg");
        info += "~";
        String info3 = info;

        info = "";
        if (raw_data.get("raw_background_x") != null)
            info += (Integer)raw_data.get("raw_background_x");
        info += "~";
        if (raw_data.get("raw_background_y") != null)
            info += (Integer)raw_data.get("raw_background_y");
        info += "~";
        if (raw_data.get("raw_background_red") != null)
            info += (Integer)raw_data.get("raw_background_red");
        info += "~";
        if (raw_data.get("raw_background_green") != null)
            info += (Integer)raw_data.get("raw_background_green");
        info += "~";
        if (raw_data.get("raw_background_blue") != null)
            info += (Integer)raw_data.get("raw_background_blue");
        info += "~";
        if (raw_data.get("raw_background_avg") != null)
            info += (Integer)raw_data.get("raw_background_avg");
        info += "~";
        String info6 = info;
        //
        //      7 = automatic glucose
        //
        info = "";
        if (raw_data.get("raw_signal_x") != null)
            info += (Integer)raw_data.get("raw_signal_x");
        info += "~";
        if (raw_data.get("raw_signal_y") != null)
            info += (Integer)raw_data.get("raw_signal_y");
        info += "~";
        if (raw_data.get("raw_signal_red") != null)
            info += (Integer)raw_data.get("raw_signal_red");
        info += "~";
        if (raw_data.get("raw_signal_green") != null)
            info += (Integer)raw_data.get("raw_signal_green");
        info += "~";
        if (raw_data.get("raw_signal_blue") != null)
            info += (Integer)raw_data.get("raw_signal_blue");
        info += "~";
        if (raw_data.get("raw_signal_avg") != null)
            info += (Integer)raw_data.get("raw_signal_avg");
        info += "~";
        String info7 = info;

        String lf_result_info = info0 + "^";
        lf_result_info += info1 + "^";
        lf_result_info += info2 + "^";
        lf_result_info += info3 + "^";
        lf_result_info += xrow[4] + "^";
        lf_result_info += xrow[5] + "^";
        lf_result_info += info6 + "^";
        lf_result_info += info7 + "^";

        return (lf_result_info);
    }
    public int[] colorsForPoint(Bitmap bitMapImage, int x, int y){
        if (x < 0){
            x = 0;
        }
        if (y<0){
            y=0;
        }
        int p = bitMapImage.getPixel(x, y);
        int[] rgbArray = new int[3];
        rgbArray[0] = Color.red(p);
        rgbArray[1] = Color.green(p);
        rgbArray[2] = Color.blue(p);
        return rgbArray;
    }

    /*
     * Converts RGB values and returns Lch values with h ranging from 0 to 2pi.
     */
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

    /*
     * Converts RGB to sRGB
     */
    public double[] sRGB(int red, int green, int blue)
    {
        int[] rgb = new int[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;

        double[] srgb = new double[3];
        for (int i = 0; i < 3 ; i ++){
            int colorRGB = rgb[i];
            double color = (double) colorRGB / 255;
            double scolor;
            if (color > 0.04045) {
                scolor = Math.pow((0.055 + color) / 1.055, 2.4);
            } else {
                scolor = color / 12.92;
            }
            srgb[i] = scolor;
        }
        return srgb;
    }

    /*
     * normalize the xyz with observer 2* and illuminant=D65
     */
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
    public String dbother_getcomment1(String category, String type)
    {
        return "";
    }
}
