package softuni.exam.models.dto;

import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DaysOfWeek;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportForecastDTO {
    @XmlElement(name = "day_of_week",required = true)
    private DaysOfWeek daysOfWeek;

    @XmlElement(name = "max_temperature")
    @Min(-20)
    @Max(60)
    private double maxTemperature;

    @XmlElement(name = "min_temperature")
    @Min(-50)
    @Max(40)
    private double minMaxTemperature;

    @XmlElement
    private String  sunrise;

    @XmlElement
    private String  sunset;

    @XmlElement
    private long city;

    public ImportForecastDTO() {
    }

    public DaysOfWeek getDaysOfWeek() {
        return daysOfWeek;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinMaxTemperature() {
        return minMaxTemperature;
    }

    public String  getSunrise() {
        return sunrise;
    }

    public String  getSunset() {
        return sunset;
    }

    public long getCity() {
        return city;
    }



}
