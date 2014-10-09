class cloud.Cloud {
    @contained
    nodes : cloud.Node[0,*]
}

class cloud.Node {
    @id
    id : String
    @contained
    softwares : cloud.Software[0,*]
}

class cloud.Software {
    @id
    name : String
    data : String[0,*]
}



