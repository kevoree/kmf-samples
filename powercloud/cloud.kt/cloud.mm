class powercloud.DistributedCloud {
    @id
    name : String
    @id
    version : String
    @contained
    clusters : powercloud.Datacenter[0,*]
    @contained
    routers : powercloud.NetworkConfiguration
    @contained
    pmSpecs : powercloud.PowerManagementSpec[0,*]

    @contained
    apps : powercloud.Apps


}

class powercloud.Datacenter {
    @id
    name : String
    @id
    version : String
    @contained
    routers : powercloud.Router[0,*]
    @contained
    nodes : powercloud.HostNode[0,*]
}



class powercloud.NetworkConfiguration {
    @id
    name : String
    @id
    version : String

    @contained
    bindings : powercloud.Router[0,*]
}



class powercloud.ConnectableNetworkNode {
    @id
    name : String
    @id
    version : String

    debut : Int

}

class powercloud.Router : powercloud.ConnectableNetworkNode,powercloud.PowerConsumer,powercloud.ResourceProvider {
    @contained
    links : powercloud.ConnectableNetworkNode[2,*]
}


class powercloud.PowerConsumerType {
    @id
    name : String
    @id
    version : String

    pmspec : powercloud.PowerManagementSpec

}

class  powercloud.PowerConsumer {
    @id
    name : String
    @id
    version : String

    cpu : powercloud.CPU
    memory : powercloud.Memory
    io : powercloud.IO

}


class powercloud.ResourceProvider {
    @id
    name : String
    @id
    version : String

    cpu : powercloud.CPU
    memory : powercloud.Memory
    io : powercloud.IO
}


class  powercloud.ResourceConsumer {
    @id
    name : String
    @id
    version : String

    cpu : powercloud.CPU
    memory : powercloud.Memory
    io : powercloud.IO

}

class  powercloud.PowerManagementSpec {
    @id
    name : String
    @id
    version : String

    cpu : powercloud.CPU
    memory : powercloud.Memory
    io : powercloud.IO

}


class  powercloud.Resource {
    @id
    name : String
    @id
    version : String
    value : Double

}

class  powercloud.CPU: powercloud.Resource {

}

class  powercloud.IO: powercloud.Resource {
}

class  powercloud.Memory: powercloud.Resource {
}


class powercloud.HostNode :  powercloud.PowerConsumer,powercloud.ResourceProvider  {

    @contained
    softwares : powercloud.VM[0,*]
}

class powercloud.VM :  powercloud.ResourceConsumer  {
    @contained
    softwares : powercloud.Component[0,*]

}


class powercloud.Apps {
    @id
    name : String
    data : String[0,*]
		
		
	@learn(-0.1)
	price : Int
}



class powercloud.Component {
    @id
    name : String
    data : String[0,*]


    @learn(-0.1)
    price : Int
}