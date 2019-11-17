package wangdaye.com.geometricweather.main.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.basic.dialog.GeoBottomSheetDialogFragment;
import wangdaye.com.geometricweather.basic.model.option.unit.PrecipitationUnit;
import wangdaye.com.geometricweather.basic.model.option.unit.ProbabilityUnit;
import wangdaye.com.geometricweather.basic.model.option.unit.TemperatureUnit;
import wangdaye.com.geometricweather.basic.model.weather.Hourly;
import wangdaye.com.geometricweather.basic.model.weather.Weather;
import wangdaye.com.geometricweather.basic.model.weather.WeatherCode;
import wangdaye.com.geometricweather.main.ui.MainColorPicker;
import wangdaye.com.geometricweather.resource.ResourceHelper;
import wangdaye.com.geometricweather.resource.provider.ResourceProvider;
import wangdaye.com.geometricweather.resource.provider.ResourcesProviderFactory;
import wangdaye.com.geometricweather.settings.SettingsOptionManager;
import wangdaye.com.geometricweather.ui.widget.AnimatableIconView;

/**
 * Hourly weather dialog.
 * */

public class HourlyWeatherDialog extends GeoBottomSheetDialogFragment {

    private CoordinatorLayout container;
    private AnimatableIconView weatherIcon;
    private MainColorPicker colorPicker;

    private Weather weather;
    private int position;

    @ColorInt private int weatherColor;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_weather_hourly, null, false);
        this.initWidget(view);
        dialog.setContentView(view);
        setBehavior(BottomSheetBehavior.from((View) view.getParent()));
        return dialog;
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void initWidget(View view) {
        if (getActivity() == null) {
            return;
        }

        ResourceProvider provider = ResourcesProviderFactory.getNewInstance();

        Hourly hourly = weather.getHourlyForecast().get(position);

        this.container = view.findViewById(R.id.dialog_weather_hourly_container);
        container.setBackgroundColor(colorPicker.getRootColor(getActivity()));

        TextView title = view.findViewById(R.id.dialog_weather_hourly_title);
        title.setText(hourly.getHour(getActivity()));
        title.setTextColor(weatherColor);

        TextView subtitle = view.findViewById(R.id.dialog_weather_hourly_subtitle);
        subtitle.setText(new SimpleDateFormat(getString(R.string.date_format_widget_long)).format(hourly.getDate()));
        subtitle.setTextColor(colorPicker.getTextSubtitleColor(getActivity()));

        view.findViewById(R.id.dialog_weather_hourly_weatherContainer).setOnClickListener(v -> weatherIcon.startAnimators());

        this.weatherIcon = view.findViewById(R.id.dialog_weather_hourly_icon);
        WeatherCode weatherCode = hourly.getWeatherCode();
        boolean daytime = hourly.isDaylight();
        weatherIcon.setAnimatableIcon(
                ResourceHelper.getWeatherIcons(provider, weatherCode, daytime),
                ResourceHelper.getWeatherAnimators(provider, weatherCode, daytime)
        );

        TextView weatherText = view.findViewById(R.id.dialog_weather_hourly_text);
        weatherText.setTextColor(colorPicker.getTextContentColor(getActivity()));

        SettingsOptionManager settings = SettingsOptionManager.getInstance(getActivity());
        TemperatureUnit temperatureUnit = settings.getTemperatureUnit();
        PrecipitationUnit precipitationUnit = settings.getPrecipitationUnit();

        StringBuilder builder = new StringBuilder(
                hourly.getWeatherText() + "  " + hourly.getTemperature().getTemperature(temperatureUnit)
        );
        if (hourly.getTemperature().getRealFeelTemperature() != null) {
            builder.append("\n")
                    .append(getString(R.string.feels_like))
                    .append(hourly.getTemperature().getRealFeelTemperature(temperatureUnit));
        }
        if (hourly.getPrecipitation().getTotal() != null) {
            Float p = hourly.getPrecipitation().getTotal();
            builder.append("\n")
                    .append(getString(R.string.precipitation))
                    .append(" : ")
                    .append(precipitationUnit.getPrecipitationText(p));
        }
        if (hourly.getPrecipitationProbability().getTotal() != null
                && hourly.getPrecipitationProbability().getTotal() > 0) {
            Float p = hourly.getPrecipitationProbability().getTotal();
            builder.append("\n")
                    .append(getString(R.string.precipitation_probability))
                    .append(" : ")
                    .append(ProbabilityUnit.PERCENT.getProbabilityText(p));
        }
        weatherText.setText(builder.toString());
    }

    public void setData(Weather weather, int position, @ColorInt int weatherColor) {
        this.weather = weather;
        this.position = position;
        this.weatherColor = weatherColor;
    }

    public void setColorPicker(@NonNull MainColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }
}
