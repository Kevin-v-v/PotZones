
# PotZones

Plugin para generar zonas que dan efectos de poción a los jugadores que permanezcan cierto tiempo en ellas.




## Documentation
### Configuración
Se pueden configurar 4 parámetros por defecto para generar las zonas.

- blocksFromCenterToBorder: cantidad de bloques hacia cada lado a los que se extiende la zona (sin contar el centro) (por ejemplo, el valor 2 genera una zona de 5*5)
- updateTimeInZone: tiempo (en ticks) en que se validará si el jugador permanece en una zona.
- effectDuration: tiempo (en segundos) que dura el efecto aplicado.
- timeToApply: tiempo (en segundos) que debe permanecer el jugador en la zona para aplicar el efecto

### Comandos
#### reload
/potzone reload 

Recarga el archivo de configuración y zonas.
#### add
/potzone add \<nombre> \<efecto> [potencia] [duración] [bloques del centro al borde] [tiempo para aplicar efecto] 

Añade una nueva zona cuadrada. Los ultimos 4 parámetros son opcionales.

- nombre: nombre de la zona
- efecto: nombre del efecto en inglés (tiene autocompletado)
- potencia: nivel del efecto
- duración: tiempo que dura el efecto aplicado
- bloques del centro al borde: cantidad de bloques hacia cada lado a los que se extiende la zona (sin contar el centro) (por ejemplo, el valor 2 genera una zona de 5*5)
- tiempo para aplicar el efecto: tiempo que debe permanecer el jugador en la zona para aplicar el efecto

#### remove
/potzone remove

Se usa estando dentro de una o mas zonas. Elimina las zonas correspondientes a la ubicación de quien lo invoca.

#### zoneinfo
/potzone zoneinfo

Se usa estando dentro de una o mas zonas. Da información general de la zona.

#### help
/potzone help

Muestra ayuda.
