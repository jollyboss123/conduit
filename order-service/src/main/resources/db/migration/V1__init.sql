CREATE SEQUENCE IF NOT EXISTS seq_order_line START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS seq_purchase_order START WITH 1 INCREMENT BY 50;

CREATE TABLE order_line
(
    id          BIGINT  NOT NULL,
    item        VARCHAR(255),
    quantity    INTEGER NOT NULL,
    total_price DECIMAL,
    order_id    BIGINT,
    status      VARCHAR(255),
    CONSTRAINT pk_orderline PRIMARY KEY (id)
);

CREATE TABLE outboxevent
(
    id             UUID         NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB        NOT NULL,
    timestamp      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_outboxevent PRIMARY KEY (id)
);

CREATE TABLE purchase_order
(
    id          BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    order_date  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_purchaseorder PRIMARY KEY (id)
);

ALTER TABLE order_line
    ADD CONSTRAINT FK_ORDERLINE_ON_ORDER FOREIGN KEY (order_id) REFERENCES purchase_order (id);