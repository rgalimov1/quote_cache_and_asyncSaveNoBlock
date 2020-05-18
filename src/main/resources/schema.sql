create table  IF NOT EXISTS QUOTE (id integer PRIMARY KEY AUTO_INCREMENT NOT NULL, isin varchar(12) NOT NULL, ask DECIMAL(15, 2), bid DECIMAL(15, 2));

create table  IF NOT EXISTS ELVL (isin varchar(12) PRIMARY KEY NOT NULL, elvl DECIMAL(15, 2));