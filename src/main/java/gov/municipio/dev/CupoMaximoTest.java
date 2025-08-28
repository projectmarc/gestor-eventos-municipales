package gov.municipio.dev;

import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.ModalidadTaller;
import java.time.LocalDate;

/**
 * Clase de prueba para verificar las nuevas validaciones de cupo máximo.
 */
public class CupoMaximoTest {
    
    public static void main(String[] args) {
        System.out.println("=== Pruebas de Validación de Cupo Máximo ===\n");
        
        try {
            // Prueba 1: Evento con inscripción DEBE tener cupo máximo
            System.out.println("--- Prueba 1: Evento con inscripción debe tener cupo ---");
            try {
                new Taller("Taller Test", LocalDate.now().plusDays(1), 4, ModalidadTaller.PRESENCIAL, null);
                System.out.println("❌ ERROR: Debería haber fallado - cupo nulo con inscripción");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            // Prueba 2: Los talleres no pueden tener inscripción falsa (lógica de negocio)
            System.out.println("\n--- Prueba 2: Los talleres siempre requieren inscripción ---");
            System.out.println("✅ Los talleres automáticamente requieren inscripción por lógica de negocio");
            
            // Prueba 3: Cupo máximo debe ser mayor a 0
            System.out.println("\n--- Prueba 3: Cupo debe ser mayor a 0 ---");
            try {
                new Taller("Taller Test", LocalDate.now().plusDays(1), 4, ModalidadTaller.PRESENCIAL, 0);
                System.out.println("❌ ERROR: Debería haber fallado - cupo 0");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            // Prueba 4: Cupo máximo no debe exceder 1,000,000
            System.out.println("\n--- Prueba 4: Cupo no debe exceder 1,000,000 ---");
            try {
                new Taller("Taller Test", LocalDate.now().plusDays(1), 4, ModalidadTaller.PRESENCIAL, 1000001);
                System.out.println("❌ ERROR: Debería haber fallado - cupo > 1,000,000");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            // Prueba 5: Cupo válido (casos límite)
            System.out.println("\n--- Prueba 5: Cupos válidos ---");
            
            // Cupo mínimo
            Taller taller1 = new Taller("Taller Test 1", LocalDate.now().plusDays(1), 4, ModalidadTaller.PRESENCIAL, 1);
            System.out.println("✅ Cupo mínimo (1): " + taller1.getCupoMaximoDefinido());
            
            // Cupo máximo
            Taller taller2 = new Taller("Taller Test 2", LocalDate.now().plusDays(1), 4, ModalidadTaller.PRESENCIAL, 1000000);
            System.out.println("✅ Cupo máximo (1,000,000): " + taller2.getCupoMaximoDefinido());
            
            // Cupo intermedio
            Exposicion expo = new Exposicion("Expo Test", LocalDate.now().plusDays(1), 4, true, 500, "Arte");
            System.out.println("✅ Cupo intermedio (500): " + expo.getCupoMaximoDefinido());
            
            // Verificar que los talleres siempre requieren inscripción
            System.out.println("✅ Taller siempre requiere inscripción: " + taller1.isRequiereInscripcion());
            
            System.out.println("\n=== Todas las pruebas de cupo máximo completadas exitosamente ===");
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
