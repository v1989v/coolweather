package com.skylan.allinweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skylan.allinweather.Utils.HttpUtils;
import com.skylan.allinweather.Utils.ParseResponse;
import com.skylan.allinweather.db.City;
import com.skylan.allinweather.db.County;
import com.skylan.allinweather.db.Province;
import com.skylan.allinweather.gson.Weather;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private Button backButton;
    private TextView titleText;
    private RecyclerView areaRecycle;
    private ProgressDialog progressDialog;
    private AreaAdapter adapter;

    private static final int TYPE_PROVINCE = 0;
    private static final int TYPE_CITY = 1;
    private static final int TYPE_COUNTY = 2;

    private  int chose_type = TYPE_PROVINCE;

    private String selectedProvinceId;
    private String selectedCityId;
    private String selectedCountyId;

    private List arrayList = new ArrayList();
    private String url = "http://guolin.tech/api/china";

    private static final String TAG = "ChooseAreaFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area , container , false);
        titleText = view.findViewById(R.id.title_area);
        backButton = view.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (chose_type) {
                    case TYPE_CITY:
                        arrayList.clear();
                        chose_type = TYPE_PROVINCE;
                        titleText.setText("中国");
                        backButton.setVisibility(View.GONE);
                        List<Province> provinces = queryProvince();
                        for (Province province : provinces) {
                            arrayList.add(province);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case TYPE_COUNTY:
                        arrayList.clear();
                        chose_type = TYPE_CITY;
                        String pid = LitePal.where("cityId=?" , selectedCityId).find(City.class).get(0).getProvinceId();
                        Province province =LitePal.where("provinceId=?" , pid).find(Province.class).get(0);
                        titleText.setText(province.getProvinceName());
                        List<City> cities = queryCity(selectedProvinceId);
                        for (City c : cities) {
                            arrayList.add(c);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        areaRecycle = view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        areaRecycle.setLayoutManager(layoutManager);
        adapter = new AreaAdapter(arrayList);
        areaRecycle.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backButton.setVisibility(View.GONE);
        titleText.setText("中国");
        showProgress("加载省份数据中...");
        List<Province> provinces = queryProvince();
        if (provinces.size() != 0) {
            for (Province province : provinces) {
                arrayList.add(province);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    closeProgress();
                }
            });
        } else {
           queryService(TYPE_PROVINCE);
        }
    }

    public List<Province> queryProvince() {
        List<Province> provinces = LitePal.findAll(Province.class);
        return provinces;
    }

    public List<City> queryCity(String provinceId) {
        List<City> cityList = LitePal.where("provinceId= ? " ,provinceId).find(City.class);
        return cityList;
    }

    public List<County> queryCounty(String cityId) {
        List<County> countyList = LitePal.where("cityId = ?" , cityId).find(County.class);
        return countyList;
    }

    public void queryService(int type) {
        switch (type) {
            case TYPE_PROVINCE:
                HttpUtils.sendHttpRequest(url , new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext() , "数据加载失败" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            ParseResponse.parseProvinceJSON(json);
                            List<Province> provinceList = queryProvince();
                            for (Province province : provinceList) {
                                arrayList.add(province);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    closeProgress();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext() ,"获取省份数据返回null" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                break;
            case TYPE_CITY:
                HttpUtils.sendHttpRequest(url+"/"+selectedProvinceId , new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext() , "数据加载失败" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            ParseResponse.parseCityJSON(json , selectedProvinceId);
                            List<City> cities = queryCity(selectedProvinceId);
                            for (City city : cities) {
                                arrayList.add(city);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    closeProgress();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext() ,"获取省份数据返回null" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                break;
            case TYPE_COUNTY:
                HttpUtils.sendHttpRequest(url + "/" + selectedProvinceId + "/" +selectedCityId , new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext() , "数据加载失败" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            ParseResponse.parseCountyJSON(json , selectedCityId);
                            List<County> counties  = queryCounty(selectedCityId);
                            closeProgress();
                            for (County county : counties) {
                                arrayList.add(county);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    closeProgress();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext() ,"获取省份数据返回null" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                break;
        }
    }
    public void showProgress(String msg) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void closeProgress() {
        progressDialog.dismiss();
    }

    class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {

        private List list;
        public AreaAdapter(List list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.area_item , viewGroup , false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.areaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = viewHolder.getAdapterPosition();
                    switch (chose_type) {
                        case TYPE_PROVINCE:
                            chose_type = TYPE_CITY;
                            backButton.setVisibility(View.VISIBLE);
                            arrayList.clear();
                            showProgress("加载城市信息...");
                            selectedProvinceId = index+1+"";
                            Province province = LitePal.where("provinceId=?" , selectedProvinceId).find(Province.class).get(0);
                            titleText.setText(province.getProvinceName());
                            List<City> cityList =queryCity(selectedProvinceId);
                            if (cityList.size() != 0) {
                                closeProgress();
                                for (City city : cityList) {
                                    arrayList.add(city);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                queryService(TYPE_CITY);
                            }
                            break;
                        case TYPE_CITY:
                            chose_type = TYPE_COUNTY;
                            backButton.setVisibility(View.VISIBLE);
                            arrayList.clear();
                            showProgress("加载县城信息");
                            City city = LitePal.where("provinceId = ?" , selectedProvinceId).find(City.class).get(index);
                            selectedCityId =city.getCityId();
                            titleText.setText(city.getCityName());
                            List<County> countyList = queryCounty(selectedCityId);
                            if (countyList.size()!= 0) {
                                closeProgress();
                                for (County county : countyList) {
                                    arrayList.add(county);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                queryService(TYPE_COUNTY);
                            }
                            break;
                        case TYPE_COUNTY:
                            String weather_id =LitePal.where("cityId=?" , selectedCityId).find(County.class).get(index).getWeatherId();
                            if (getActivity() instanceof WeatherActivity) {
                                WeatherActivity activity = (WeatherActivity) getActivity();
                                activity.refresh(weather_id);
                                activity.drawerLayout.closeDrawers();
                            }else {
                                WeatherActivity.startAttch(getContext() , weather_id);
                                break;
                            }
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            switch (chose_type) {
                case ChooseAreaFragment.TYPE_PROVINCE:
                    viewHolder.viewLine.setVisibility(View.VISIBLE);
                    if (i == (list.size()-1)) {
                        viewHolder.viewLine.setVisibility(View.GONE);
                    }
                    Province province = (Province) list.get(i);
                    viewHolder.areaText.setText(province.getProvinceName());
                    break;
                case ChooseAreaFragment.TYPE_CITY:
                    viewHolder.viewLine.setVisibility(View.VISIBLE);
                    if (i == (list.size()-1)) {
                        viewHolder.viewLine.setVisibility(View.GONE);
                    }
                    City city = (City) list.get(i);
                    viewHolder.areaText.setText(city.getCityName());
                    break;
                case ChooseAreaFragment.TYPE_COUNTY:
                    viewHolder.viewLine.setVisibility(View.VISIBLE);
                    if (i == (list.size()-1)) {
                        viewHolder.viewLine.setVisibility(View.GONE);
                    }
                    County county = (County) list.get(i);
                    viewHolder.areaText.setText(county.getCountyName());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView areaText;
            private View viewLine;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                areaText = itemView.findViewById(R.id.area_text);
                viewLine = itemView.findViewById(R.id.view_line);
            }
        }
    }
}
