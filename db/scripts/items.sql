create table if not exists items(
    id serial primary key,
    name varchar(200) not null constraint unique_item_name unique,
    description varchar(2000),
    created timestamp not null ,
    done timestamp default null,
    user_id int not null references users(id)
)