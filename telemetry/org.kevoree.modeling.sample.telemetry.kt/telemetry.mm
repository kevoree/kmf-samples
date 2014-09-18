class org.kevoree.telemetry.store.TelemetryStore {
    @contained
    topics : org.kevoree.telemetry.store.Topic[0,*]
}

class org.kevoree.telemetry.store.Topic {
    @id
    name : String
    @contained
    topics : org.kevoree.telemetry.store.Topic[0,*]
    @contained
    ticket : org.kevoree.telemetry.store.Ticket
}

class org.kevoree.telemetry.store.Ticket {
    origin:String
    type:String
    message:String
    stack:String
}

