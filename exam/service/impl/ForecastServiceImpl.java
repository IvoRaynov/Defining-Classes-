package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportForecastDTO;
import softuni.exam.models.dto.ImportForecastRootDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {
    //src/main/resources/files/xml/forecasts.xml
    private final Path path = Path.of("src", "main", "resources", "files", "xml", "forecasts.xml");
    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;


    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository) throws JAXBException {

        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;

        JAXBContext context = JAXBContext.newInstance(ImportForecastRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();

        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

        this.modelMapper = new ModelMapper();
        this.modelMapper.addConverter(ctx-> LocalTime.parse(ctx.getSource(), DateTimeFormatter.ofPattern("HH:mm:ss")),String.class,LocalTime.class);
    }

    @Override
    public boolean areImported() {

        return this.forecastRepository.count()>0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        ImportForecastRootDTO importForecastDTOS = (ImportForecastRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return importForecastDTOS.getForecast().stream().map(this::importForecast).collect(Collectors.joining("\n"));
    }

    private String  importForecast(ImportForecastDTO dto) {
        Set<ConstraintViolation<ImportForecastDTO>> errors = this.validator.validate(dto);
        if(dto.getDaysOfWeek()==null){
            return "Invalid forecast";
        }
        if(!errors.isEmpty()){
            return "Invalid forecast";
        }
        Optional<Forecast> optionalForecast = this.forecastRepository.findByDaysOfWeekAndCity(dto.getDaysOfWeek(), dto.getCity());
        if(optionalForecast.isPresent()){
            return "Invalid forecast";
        }


        Optional<City> city = this.cityRepository.findById(dto.getCity());

        Forecast forecast = this.modelMapper.map(dto, Forecast.class);

        forecast.setCity(city.get());

        this.forecastRepository.save(forecast);
        return "Successfully import forecast " + forecast.getDaysOfWeek() +" - " + forecast.getMaxTemperature();
    }


    @Override
    public String exportForecasts() {
        return null;
    }
}
