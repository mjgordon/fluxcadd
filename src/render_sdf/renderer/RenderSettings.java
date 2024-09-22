package render_sdf.renderer;

public class RenderSettings {
	public boolean useNormalShading = true;
	
	public boolean useReflectivity = true;
	
	public boolean useShadows = true;
	
	
	public RenderSettings() {
		
	}
	
	public RenderSettings(boolean useNormalShading, boolean useReflectivity, boolean useShadows) {
		this.useNormalShading = useNormalShading;
		this.useReflectivity = useReflectivity;
		this.useShadows = useShadows;
	}
}
