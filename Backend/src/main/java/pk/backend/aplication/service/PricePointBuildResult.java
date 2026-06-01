package pk.backend.aplication.service;

public record PricePointBuildResult(
        long parcelPoints,
        long buildingPoints,
        long localPoints,
        long finalDataPoints
) {

    public long totalPoints() {
        return parcelPoints + buildingPoints + localPoints;
    }
}
