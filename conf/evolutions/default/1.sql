#----- !Ups
create table users(
 u_id int not null auto_increment,u_fname varchar(50),u_mname varchar(50),
u_lname varchar(50),u_uname varchar(50),u_email varchar(50)not null unique,u_pass varchar(50),
u_cpass varchar(50),u_mobile varchar(20),u_gender varchar(20),u_age int,u_hobby varchar(20),
u_enable boolean, primary key(u_id));

 create table test(id int not null auto_increment,title varchar(200) not null,
 description varchar(500) not null,primary key (id));

#----- !Downs
drop table users;
drop table test;
