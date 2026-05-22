
@Entity
> Utilizado para establecer que la clase sera una entidad dentro de la base de datos


@Table(name = "unString") // unString deberia de ser el nombre de la clase 


@Id
> Variable inmediatamente abajo es la pk


> todas las entidades tienen que tener un constructor vacio para que funcione con el ORM

> Redefinir el Equals y el toString dentro de cada entity del JPA


## Anotaciones Formato FS
bea/target                              <- No se toca
bea/src/test                            <- Tests
bea/src/main/resources/front-bea        <- FrontEnd
bea/src/main/java/.../demo              <- BackEnd y logica en si
bea/src/main/java/.../demo/model        <- Las clases
bea/src/main/java/.../demo/controller   <- Los controladores (conectores entre front y back)
bea/src/main/java/.../demo/service      <- Los servicios
