package pk.backend.infrastructure.adapter;

import org.springframework.web.bind.annotation.RequestParam;
import pk.backend.aplication.port.outbound.MapFilter;
import pk.backend.domain.box.BoxValue;
import pk.backend.domain.utils.CompareCondition;
import pk.backend.infrastructure.MapType;

import java.util.List;

public class MapMapper {

    private MapMapper() {
    }

    public static List<Class<? extends BoxValue>> mapMapTypes(List<MapType> mapType){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static MapFilter mapToFilter(float value, CompareCondition condition){
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
