package pathtracer;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;

/**
 *
 * @author balsfull
 */
public class Scene {
    
    private Element[] elements;
    private Camera camera;
    private final Map<UUID, DoubleFunction<Optional<Element>>> elementSupplier;
    
    public Scene() {
        this.elementSupplier = new HashMap<>();
    }
    
    public Optional<Entry<Element, Intersection>> intersect(Ray ray) {
        Entry<Element, Intersection> result = null;
        double distance = Double.POSITIVE_INFINITY;
        for(Element e : elements) {
            Optional<Intersection> inter = e.getSurface().intersect(ray);
            if(inter.isPresent()) {
                double d = inter.get().getDistance();
                if(d < distance) {
                    result = new SimpleEntry(e, inter.get());
                    distance = d;
                }
            }
        }
        return Optional.ofNullable(result);
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setTime(double time) {
        elements = elementSupplier.values()
                .stream()
                .map(f -> f.apply(time))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()).toArray(new Element[0]);
    }
    
    public UUID addElement(Element element) {
        return addElement((d) -> Optional.of(element));
    }
    
    public UUID addElement(DoubleFunction<Optional<Element>> function) {
        UUID uuid = UUID.randomUUID();
        elementSupplier.put(uuid, function);
        return uuid;
    }
    
    public DoubleFunction<Optional<Element>> removeElement(UUID uuid) {
        return elementSupplier.remove(uuid);
    }
}
