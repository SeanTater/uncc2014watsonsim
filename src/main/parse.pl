is_type_statement(A, B) :-
    (nsubj(A, B), cop(B, _), det(B, _));
    (nsubj(B, A), cop(B, _), det(B, _)).

/* A plain type */
type_a(Name, Type) :-
    is_type_statement(Name, Type),
    det(Type, _).

/* A type with subject conjunctions */
type_b(Name, Type) :-
    type_a(Name, Type) ;
    (type_a(AnotherName, Type),
     conj_and(AnotherName, Name)).

/* A type with subject or type conjunctions */
type_c(Name, Type) :-
    type_b(Name, Type) ;
    (type_b(Name, AnotherType),
     conj_and(AnotherType, Type)).
