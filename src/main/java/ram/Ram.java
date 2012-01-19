package seta.infoapi;

public class Ram extends InfoApiEndpoint {
	String endpoint "/ram";
	String docString = "Returns the available ram of the server in the format: Total / Free / Max";
	
	public HttpResponse methodGet() {
		return new HttpContentResponse(getRuntimeMemoryInformationAsString());
	}

	private String getRuntimeMemoryInformationAsString() {
		String returnString = "";

		// Total Memory of Java Runtime in MB
		Double totalMemory = Math.floor((Runtime.getRuntime().totalMemory() / Math.pow(10, 6)));

		// Free Memory of Java Runtime in MB
		Double freeMemory = Math.floor((Runtime.getRuntime().freeMemory() / Math.pow(10, 6)));

		// Maximum Memory of Java Runtime in MB
		Double maxMemory = Math.floor((Runtime.getRuntime().maxMemory() / Math.pow(10, 6)));

		// Returns totalMemory, freeMemory and maxMemory - separated by slash
		returnString = totalMemory.toString() + "/" + freeMemory.toString() + "/" + maxMemory.toString();

		return returnString;
    }

}
