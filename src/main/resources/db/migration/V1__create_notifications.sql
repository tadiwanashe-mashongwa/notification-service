CREATE TABLE notification_orders (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    read BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_notification_order_type UNIQUE (order_id, type)
);
