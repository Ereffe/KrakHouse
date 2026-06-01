package pk.backend.aplication.service;

public record PricePointBuildResult(
        long parcelPoints,
        long buildingPoints
) {

    public long totalPoints() {
        return parcelPoints + buildingPoints;
    }
}
