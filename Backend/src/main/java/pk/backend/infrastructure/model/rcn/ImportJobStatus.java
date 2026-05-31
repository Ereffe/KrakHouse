package pk.backend.infrastructure.model.rcn;


public enum ImportJobStatus {
    CREATED,
    DOWNLOADING,
    DOWNLOADED,
    PARSING,
    LINKING,
    DONE,
    FAILED
}