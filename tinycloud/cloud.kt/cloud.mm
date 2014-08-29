class cloud.Cloud {
    @contained
    nodes : cloud.Node[0,*]

    soft : cloud.Software
}

class cloud.Node {
    @id
    id : String
    @id
    version : String
    @contained
    softwares : cloud.Software[0,*]
    @contained
    hosted : cloud.Node[0,*]
    @contained
    refs : cloud.Node[0,*]
}

class cloud.Software {
    @id
    name : String
    data : String[0,*]
		
		
	@learn(-0.1)
	price : Int
}