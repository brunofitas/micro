

CREATE TABLE IF NOT EXISTS address(
    id uuid PRIMARY KEY,
    line1 varchar(200),
    line2 varchar(200) NULL,
    post_code varchar(200),
    city varchar(200),
    country varchar(200)
);


CREATE TABLE IF NOT EXISTS organisation(
    id uuid PRIMARY KEY,
    name varchar(200),
    email varchar(200) NULL,
    type varchar(200)
);


CREATE TABLE IF NOT EXISTS userprofile(
    id uuid PRIMARY KEY,
    first_name varchar(200),
    last_name varchar(200),
    email varchar(200) NULL,
    salutation varchar(200),
    telephone varchar(200),
    type varchar(200),
    organisation_id uuid REFERENCES organisation(id)
);


