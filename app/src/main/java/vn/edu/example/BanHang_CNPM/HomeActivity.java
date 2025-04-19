package vn.edu.example.BanHang_CNPM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.example.BanHang_CNPM.Adapter.Adapter_LoaiSP;
import vn.edu.example.BanHang_CNPM.Adapter.Adapter_SPMoi;
import vn.edu.example.BanHang_CNPM.DTO.DTO_LoaiSP;


import vn.edu.example.BanHang_CNPM.DTO.DTO_SPMoi;
import vn.edu.example.BanHang_CNPM.DTO.DTO_ThongTinNguoiDung;
import vn.edu.example.BanHang_CNPM.Model.GioHang;
import vn.edu.example.BanHang_CNPM.Server.Server;


public class HomeActivity extends AppCompatActivity {
private Toolbar toolbar;
private ViewFlipper vfl_Home;
private GridView grv_spmoi;
private NavigationView nav_View;
private ListView lv_menu;
private DrawerLayout drawerLayout;
private Adapter_LoaiSP adapter_loaiSP;
private Adapter_SPMoi adapter_spMoi;
private List<DTO_LoaiSP> list_loaisp;
private List<DTO_ThongTinNguoiDung>list_thongtin;
private List<DTO_SPMoi>list_spmoi;
private NotificationBadge badge;
private FrameLayout frame_giohang;
private ImageView img_timkiem;
// Loại sản phẩm
    int id_loaisp=0;
    String tenloaisp="";
    String hinhanhloaisp="";
// Sản phẩm mới nhất
int id_spmoi=0;
    String ten_spmoi="";
    String hinh_spmoi="";
    String gia_spmoi="";
    String mota="";
    int id_loaisp_fk=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Trang Chủ");

        AnhXa();
        ActionBar();
        ActionViewFlipper();
        ClickMenu();
        if(Check_Internet(this)){

           getLoaiSP();
            getSPMoi();
        }else {
           Toast.makeText(getApplicationContext(),"không có kết nối internet",Toast.LENGTH_SHORT).show();
        }

    }
    private void AnhXa(){
        toolbar=findViewById(R.id.toolbar);
        vfl_Home=findViewById(R.id.vfl_home);
        grv_spmoi=findViewById(R.id.grv_spmoi);
        nav_View=findViewById(R.id.nav_view);
        lv_menu=findViewById(R.id.lv_menu);
        drawerLayout=findViewById(R.id.drawerlayout);
        frame_giohang=findViewById(R.id.frame_giohang);
        img_timkiem=findViewById(R.id.img_timkiem);
        badge=findViewById(R.id.ntf_soluong);
        int dataid=getIntent().getIntExtra("id_nguoidung",0);
        String dataemail=getIntent().getStringExtra("email");
        String dataphone=getIntent().getStringExtra("phone");

        list_thongtin=new ArrayList<>();
        list_thongtin.add(new DTO_ThongTinNguoiDung(dataid,dataemail,dataphone));


        //chuyển màn hình giỏ hàng
        frame_giohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent giohang=new Intent(getApplicationContext(),GioHangActivity.class);
                giohang.putExtra("id_nguoidung",dataid);
                giohang.putExtra("email",dataemail);
                giohang.putExtra("phone",dataphone);
                startActivity(giohang);
            }
        });
        //chuyển mà hình tìm kiếm
        img_timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timkiem=new Intent(HomeActivity.this,TimKiemActivity.class);
                timkiem.putExtra("id_nguoidung",dataid);
                timkiem.putExtra("email",dataemail);
                timkiem.putExtra("phone",dataphone);
                startActivity(timkiem);
            }
        });

        //khởi tạo list
        list_loaisp=new ArrayList<>();
        list_spmoi=new ArrayList<>();
        //khởi tạo adapter
        adapter_loaiSP=new Adapter_LoaiSP(getApplicationContext(),list_loaisp);
        lv_menu.setAdapter(adapter_loaiSP);
        adapter_spMoi=new Adapter_SPMoi(getApplicationContext(),list_spmoi,list_thongtin);
        grv_spmoi.setAdapter(adapter_spMoi);
        //khởi tạo list giỏ hàng

        if(GioHang.list_giohang==null){
            GioHang.list_giohang=new ArrayList<>();

        }else {
            badge.setText(String.valueOf(GioHang.list_giohang.size()));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        badge.setText(String.valueOf(GioHang.list_giohang.size()));
    }
    private void ActionBar(){
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);

            }
        });
    }
    private void ActionViewFlipper(){
        List<String>list_quangcao=new ArrayList<>();
        list_quangcao.add("https://kimhungmarket.com/uploads/users/users391/logo-rau-cu-qua-24h-ai-01.png");
        list_quangcao.add("https://thietkehaithanh.com/wp-content/uploads/2013/08/thietkehaithanh-banner-1-800x292.jpg");
        list_quangcao.add("https://18fit.vn/uploads/files/2022/09/19/rau-cu-qua-1.png");

        for (int i=0;i<list_quangcao.size();i++){
            ImageView img_quangcao=new ImageView(getApplicationContext());

            Glide.with(getApplicationContext()).load(list_quangcao.get(i)).into(img_quangcao);
            img_quangcao.setScaleType(ImageView.ScaleType.FIT_XY);
            vfl_Home.addView(img_quangcao);
        }
        vfl_Home.setFlipInterval(5000);
        vfl_Home.setAutoStart(true);
        Animation slide_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        vfl_Home.setInAnimation(slide_in);
        vfl_Home.setOutAnimation(slide_out);
    }
    private boolean Check_Internet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobie= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi != null && wifi.isConnected() || (mobie!= null && mobie.isConnected())){
            return  true;

        }else {
            return false;

        }
    }
    private void getLoaiSP(){

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //JsonArrayRequest(duongDan,thanhcong,thatbai)
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Server.duongdan_loaisp, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                if (response != null) {  // Kiểm tra nếu response không phải null
                    try {
                        // Kiểm tra nếu response là một JSONArray
                        if (response instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) response;  // Cast response thành JSONArray

                            // Kiểm tra xem mảng JSON có ít nhất một phần tử
                            if (jsonArray.length() > 0) {
                                // Lấy mảng con đầu tiên trong mảng chính (vì dữ liệu của bạn là một mảng con)
                                JSONArray subArray = jsonArray.getJSONArray(0);  // Lấy mảng con đầu tiên

                                // Duyệt qua tất cả các phần tử trong mảng con
                                for (int i = 0; i < subArray.length(); i++) {
                                    JSONObject jsonObject = subArray.getJSONObject(i);  // Lấy đối tượng JSON tại vị trí i

                                    // Lấy dữ liệu từ các thuộc tính trong jsonObject
                                    int id_loaisp = jsonObject.getInt("id_loaisp");  // Lấy id
                                    String tenloaisp = jsonObject.getString("tenloaisp");  // Lấy tên loại
                                    String hinhanhloaisp = jsonObject.getString("hinhloaisp");  // Lấy hình ảnh

                                    // Thêm đối tượng vào list_loaisp
                                    list_loaisp.add(new DTO_LoaiSP(id_loaisp, tenloaisp, hinhanhloaisp));
                                }

                                // Cập nhật adapter sau khi thêm tất cả đối tượng
                                adapter_loaiSP.notifyDataSetChanged();

                                // Log danh sách sau khi đã thêm các phần tử
                                Log.d("List_DS", list_loaisp.toString());

                                // Thêm các phần tử "Đơn Hàng" và "Đăng Xuất" vào danh sách
                                if (list_loaisp.size() <= 3) {
                                    list_loaisp.add(new DTO_LoaiSP(11, "Đơn Hàng", "https://cdn-icons-png.flaticon.com/128/6769/6769651.png"));
                                }

                                if (list_loaisp.size() <= 4) {
                                    list_loaisp.add(new DTO_LoaiSP(12, "Đăng Xuất", "https://cdn-icons-png.flaticon.com/128/5794/5794974.png"));
                                }

                                // Log danh sách sau khi thêm các phần tử "Đơn Hàng" và "Đăng Xuất"
                                Log.d("so", list_loaisp.toString());
                            } else {
                                Log.e("JSON Error", "Expected a sub-array in response");
                            }
                        } else {
                            // Xử lý khi response không phải JSONArray
                            Log.e("JSON Error", "Expected JSONArray but received something else");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }

        }, new Response.ErrorListener() {
            //nếu thất bại
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private  void getSPMoi(){
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //JsonArrayRequest(duongDan,thanhcong,thatbai)
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Server.duongdan_spmoi, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null){// nếu lấy kết quả về khác null
                    for (int i = 0; i < response.length(); i++){//dua vao vong lap
                        try {
                            JSONObject jsonObject=response.getJSONObject(i);
                            Log.d("SPMoi", jsonObject.toString()); // Log dữ liệu sản phẩm mới
                            id_spmoi=jsonObject.getInt("id_spmoi");
                            ten_spmoi=jsonObject.getString("ten_spmoi");
                            hinh_spmoi=jsonObject.getString("hinh_spmoi");
                            gia_spmoi=jsonObject.getString("gia_spmoi");
                            mota=jsonObject.getString("mota");
                            id_loaisp_fk=jsonObject.getInt("id_loaisp");
                            list_spmoi.add(new DTO_SPMoi(id_spmoi,ten_spmoi,hinh_spmoi,gia_spmoi,mota,id_loaisp_fk));//dua vao mang
                            Log.d("size", String.valueOf(list_spmoi.size()));
                            adapter_spMoi.notifyDataSetChanged();

//                            Toast.makeText(getApplicationContext(),ten_spmoi,Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();

                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private  void ClickMenu(){
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int dataid=getIntent().getIntExtra("id_nguoidung",0);
                String dataemail=getIntent().getStringExtra("email");
                String dataphone=getIntent().getStringExtra("phone");
              switch (position){

                  case 0://điện thoại



                      break;
                  case 1://laptop


                      break;
                  case 2:
                      //liên hệ

                      break;
                  case 3://đơn hàng
                      Intent donhang=new Intent(getApplicationContext(),DonHangActivity.class);
                      donhang.putExtra("id_nguoidung",dataid);
                      startActivity(donhang);
                      break;
                  case 4:
                      //đăng xuất

                      Intent dangxuat=new Intent(getApplicationContext(),DangNhapActivity.class);
                      startActivity(dangxuat);
                      finish();
                      break;

              }
            }
        });
    }


}