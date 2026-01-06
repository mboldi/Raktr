CREATE TABLE container_devices (
    container_id BIGINT NOT NULL REFERENCES scannables(id),
    device_id BIGINT NOT NULL REFERENCES scannables(id),
    PRIMARY KEY (container_id, device_id)
);

CREATE INDEX idx_container_devices_container ON container_devices(container_id);
CREATE INDEX idx_container_devices_device ON container_devices(device_id);
