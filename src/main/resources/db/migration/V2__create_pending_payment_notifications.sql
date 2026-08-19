CREATE TABLE pending_payment_notifications (
    order_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    PRIMARY KEY (order_id, type)
);
