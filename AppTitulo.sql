drop database if exists apptitulo;
create database apptitulo;
use apptitulo;

select @pss:='gatin';

CREATE TABLE tipos_usuarios(
  id INT NOT NULL,
  descripcion TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE usuarios(
  id BIGINT NOT NULL AUTO_INCREMENT,
  correo VARCHAR(100) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  contraseña blob NOT NULL,
  fecha_registro DATETIME NOT NULL,
  tipo_usuario INT NOT NULL,
  path_img TEXT,
  recovery_random VARCHAR(130),
  PRIMARY KEY (id,correo),
  FOREIGN KEY (tipo_usuario) REFERENCES tipos_usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE learning_objects(
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(200),
  descripcion TEXT,
  url TEXT,
  fecha_ingreso DATETIME NOT NULL,
  usuario_correo VARCHAR(100) NOT NULL,
  id_usuario BIGINT NOT NULL,
  ranking_topicos TEXT,
  PRIMARY KEY (id),
  FOREIGN KEY (id_usuario,usuario_correo) REFERENCES usuarios(id,correo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE log_rdf(
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_archivo DATETIME NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE log_recategorization(
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_recategorizacion DATETIME NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE log_topicos(
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_modificacion DATETIME NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO tipos_usuarios VALUES (1,'Administrador');
INSERT INTO tipos_usuarios VALUES (2,'Miembro');

INSERT INTO usuarios (correo, nombre, contraseña, fecha_registro, tipo_usuario, path_img) VALUES
('admin1@scanoa.com', 'admin1', 0xf01d5a484c6df4c70fa50a683e37ebd2, '2014-06-25 00:00:00', 1, 'img_default.jpeg'),
('admin2@scanoa.com', 'admin2', 0x0f17337ab51640400f95b56ae8d6c135, '2014-06-25 00:00:00', 1, 'img_default.jpeg'),
('admin3@scanoa.com', 'admin3', 0x853c2610b55dd76975ce1882ec7aeebd, '2014-06-25 00:00:00', 1, 'img_default.jpeg'),
('admin4@scanoa.com', 'admin4', 0x0dd447fe3f994fe4ff22a5fe38198443, '2014-06-25 00:00:00', 1, 'img_default.jpeg'),
('admin5@scanoa.com', 'admin5', 0x702b5b2628eb0f832605a3c0ef257aa9, '2014-06-25 00:00:00', 1, 'img_default.jpeg'),
('user1@scanoa.com', 'user1', 0xea7394b7f5b3e06a21ae8395d392f8a4, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user2@scanoa.com', 'user2', 0x96447e4bc5ee50b9a275fa0bb45fa050, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user3@scanoa.com', 'user3', 0x3d761e86224ae8d0b0fef25f997b99a9, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user4@scanoa.com', 'user4', 0xa142ca591cc0f17628809d0c6d78437f, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user5@scanoa.com', 'user5', 0x1f997d21a60aad6d9135d648c4812935, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user6@scanoa.com', 'user6', 0xfba627b9da87d2b5edd754427982ac76, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user7@scanoa.com', 'user7', 0x296aad16ce30ccf3b96edce4484ae58e, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user8@scanoa.com', 'user8', 0xd38ff67529493308be4cb965a24aa6cd, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user9@scanoa.com', 'user9', 0xfdfdccc702a70b132d63871729c63a2e, '2014-06-25 00:00:00', 2, 'img_default.jpeg'),
('user10@scanoa.com', 'user10', 0x2a31c577868bb7a204df410804e30e0b, '2014-06-25 00:00:00', 2, 'img_default.jpeg');

INSERT INTO log_recategorization (fecha_recategorizacion) VALUES
('2014-06-25 00:00:00');

INSERT INTO log_topicos (fecha_modificacion) VALUES
('2014-06-25 00:00:00');