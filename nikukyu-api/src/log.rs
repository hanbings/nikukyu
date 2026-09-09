use tracing_subscriber::{EnvFilter, layer::SubscriberExt, util::SubscriberInitExt};

pub struct RequestSpan;

impl tracing_actix_web::RootSpanBuilder for RequestSpan {
    fn on_request_start(request: &actix_web::dev::ServiceRequest) -> tracing::Span {
        // OAuth callbacks contain credentials in their query string. Never log it.
        tracing::info_span!("http.request", http.method = %request.method(), http.target = request.path(),
            http.status_code = tracing::field::Empty, otel.status_code = tracing::field::Empty,
            exception.message = tracing::field::Empty, exception.details = tracing::field::Empty)
    }

    fn on_request_end<B: actix_web::body::MessageBody>(
        span: tracing::Span,
        outcome: &Result<actix_web::dev::ServiceResponse<B>, actix_web::Error>,
    ) {
        tracing_actix_web::DefaultRootSpanBuilder::on_request_end(span, outcome);
    }
}

pub fn init_tracing(default_filter: &str) {
    let filter =
        EnvFilter::try_from_default_env().unwrap_or_else(|_| EnvFilter::new(default_filter));

    tracing_subscriber::registry()
        .with(filter)
        .with(tracing_subscriber::fmt::layer().pretty())
        .init();
}
