:-	dynamic minimo/1.
:-	dynamic proximo_passo/2.
:-	dynamic tamanho_mapa/2.
:-	dynamic mapa_real/2.
:-	dynamic inimigo/2.
:-	dynamic total_ouros/1.

:-	dynamic sensor/3.

:- dynamic  posicao/2.

:- dynamic  orientacao/1.

:- dynamic  score/1.

:- dynamic  municao/1.

:- dynamic  energia/1.

:- dynamic  mapa_agente/2.

instanciar_tamanho_mapa(X, Y) :-	assert(tamanho_mapa(X, Y)).

adicionar_ao_mapa((X, Y), Objeto) :- assert(mapa_real( (X, Y) , Objeto)).

acao(mover_para_frente) :- 	orientacao(direita), posicao(X, Y), Z is X + 1, posicao_pertence_mapa(Z, Y), retract(posicao(X, Y)), assert(posicao(Z, Y)), ( mapa_real((Z, Y), Objeto), findall(_,assert_certeza((Z, Y), Objeto),_); findall(_,assert_certeza((Z, Y), nada),_) ) , findall(_,observa(Z, Y), _), findall(_,atualiza_duvidas(),_), adiciona_a_score(-1), findall(_, transcore_eventos(), _), ! .
acao(mover_para_frente) :-	orientacao(esquerda), posicao(X, Y), Z is X - 1, posicao_pertence_mapa(Z, Y), retract(posicao(X, Y)), assert(posicao(Z, Y)), ( mapa_real((Z, Y), Objeto), findall(_,assert_certeza((Z, Y), Objeto),_); findall(_,assert_certeza((Z, Y), nada),_) ) , findall(_,observa(Z, Y), _), findall(_,atualiza_duvidas(),_), adiciona_a_score(-1), findall(_, transcore_eventos(), _), ! .
acao(mover_para_frente) :-	orientacao(cima), posicao(X, Y), Z is Y + 1, posicao_pertence_mapa(X, Z), retract(posicao(X, Y)), assert(posicao(X, Z)), ( mapa_real((X, Z), Objeto), findall(_,assert_certeza((X, Z), Objeto),_) ; findall(_,assert_certeza((X, Z), nada),_) ) , findall(_,observa(X, Z),_), findall(_,atualiza_duvidas(),_), adiciona_a_score(-1), findall(_, transcore_eventos(), _), ! .
acao(mover_para_frente) :-	orientacao(baixo), posicao(X, Y), Z is Y - 1, posicao_pertence_mapa(X, Z), retract(posicao(X, Y)), assert(posicao(X, Z)), ( mapa_real((X, Z), Objeto), findall(_,assert_certeza((X, Z), Objeto),_); findall(_,assert_certeza((X, Z), nada),_) ) , findall(_,observa(X, Z),_), findall(_,atualiza_duvidas(),_), adiciona_a_score(-1), findall(_, transcore_eventos(), _), ! .

acao(virar_a_direita) :- orientacao(cima), retract(orientacao(cima)), assert(orientacao(direita)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(esquerda), retract(orientacao(esquerda)), assert(orientacao(cima)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(baixo), retract(orientacao(baixo)), assert(orientacao(esquerda)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(direita), retract(orientacao(direita)), assert(orientacao(baixo)), adiciona_a_score(-1), !.

acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), ouro), findall(_,assert_certeza( (X, Y), nada),_) , retract(mapa_real((X, Y), ouro)), assert(mapa_real((X, Y), nada)), adiciona_a_score(1000), total_ouros(O), retract(total_ouros(_)), K is O - 1, assert(total_ouros(K)), !.
acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), power_up), findall(_,assert_certeza( (X, Y), nada),_), retract(mapa_real((X, Y), power_up)), assert(mapa_real((X, Y), power_up)), recuperar_vida(), !. 

acao(subir) :-	posicao(X, Y), X = 1, Y = 1, write('O Agente saiu da caverna, que fim glorioso').

acao(atirar) :-	municao(X), X > 0, adiciona_a_score(-10), findall(_,simula_tiro(),_), remove_municao().

acao(decidir) :-	total_ouros(X), X =< 0, findall(_,retract(proximo_passo(_,_)),_), findall(_, encontrar_mais_proximo(certeza(saida)) ,_), !.
acao(decidir) :-	posicao(Z, W), mapa_agente((Z, W), certeza(ouro)), acao(pegar_objeto), acao(decidir), !.
acao(decidir) :-	energia(V), V =< 50, ( posicao(Z, W), mapa_agente((Z, W), certeza(power_up)), acao(pegar_objeto), acao(decidir) ; (mapa_agente((_, _), certeza(power_up)), findall(_,retract(proximo_passo(_,_)),_), findall(_, encontrar_mais_proximo(certeza(power_up)) ,_ ) , !) ).
acao(decidir) :-	( mapa_agente((_,_), duvida(nada)), findall(_,retract(proximo_passo(_,_)),_), findall(_, encontrar_mais_proximo(duvida(nada)) ,_ )  ), !.
acao(decidir) :-	write("ola"),agente_olhando_para(X, Y), posicao(Z, W), distancia_manhatam((Z,W), (X, Y), Custo), atualiza_proximo_passo((X, Y), Custo), !.

encontrar_mais_proximo(Objeto) :-	assert(minimo(1000)), findall(_,menor_distancia(Objeto),_), retract(minimo(_)), !.

menor_distancia(Objeto) :-	mapa_agente((X, Y), Objeto), posicao(Z, W), distancia_manhatam((Z,W), (X, Y), Custo) , atualiza_proximo_passo((X, Y), Custo).

observa(X, Y) :-	(Z is X + 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up, findall(_,atualiza_mapa_agente( (X, Y), Objeto),_) ;
					Z is X - 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up, findall(_,atualiza_mapa_agente( (X, Y), Objeto),_) ;
					Z is Y + 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up, findall(_,atualiza_mapa_agente( (X, Y), Objeto),_) ;
					Z is Y - 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up, findall(_,atualiza_mapa_agente( (X, Y), Objeto),_)).
					
						
observa(X, Y) :-	not((Z is X + 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is X - 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is Y + 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is Y - 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up)) ,
						findall(_,atualiza_mapa_agente((X, Y), nada),_).
										
assert_certeza( (X, Y), Objeto ) :-	  ( retract(mapa_agente( (X, Y), _ )) ; ( assert(mapa_agente((X, Y), certeza(Objeto))) , ! ) ) , ( sensor((_,_), (X, Y), _) , findall(_, retract(sensor((_,_), (X, Y), _)), _) ).
			
atualiza_mapa_agente( (X, Y), nada) :- 		(Z is X + 1, posicao_pertence_mapa(Z, Y), not(mapa_agente( (Z, Y) , certeza(_))), retract(mapa_agente((Z, Y) , duvida(_))), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
											Z is X - 1, posicao_pertence_mapa(Z, Y), not(mapa_agente( (Z, Y) , certeza(_))), retract(mapa_agente((Z, Y) , duvida(_))), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
											Z is Y + 1, posicao_pertence_mapa(X, Z), not(mapa_agente( (X, Z) , certeza(_))), retract(mapa_agente((X, Z) , duvida(_))), assert(mapa_agente( (X, Z) , duvida(nada) )) ;
											Z is Y - 1, posicao_pertence_mapa(X, Z), not(mapa_agente( (X, Z) , certeza(_))), retract(mapa_agente((X, Z) , duvida(_))), assert(mapa_agente( (X, Z) , duvida(nada) ))),!.
											
atualiza_mapa_agente( (X, Y), Objeto) :- 	(Z is X + 1, W is Y, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), not(adjacente_a((X, Y), Objeto)), adicionar_sensor((X, Y), (Z, W), Objeto),  not(mapa_agente( (Z, W) , duvida(Objeto) )) ), assert(mapa_agente( (Z, W) , duvida(Objeto) )) ;
											Z is X - 1, W is Y, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), not(adjacente_a((X, Y), Objeto)), adicionar_sensor((X, Y), (Z, W), Objeto), not(mapa_agente( (Z, W) , duvida(Objeto) )) ), assert(mapa_agente( (Z, W) , duvida(Objeto) )) ;
											Z is X, W is Y + 1, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), not(adjacente_a((X, Y), Objeto)), adicionar_sensor((X, Y), (Z, W), Objeto), not(mapa_agente( (Z, W) , duvida(Objeto) )) ), assert(mapa_agente( (Z, W) , duvida(Objeto) )) ;
											Z is X, W is Y - 1, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), not(adjacente_a((X, Y), Objeto)), adicionar_sensor((X, Y), (Z, W), Objeto), not(mapa_agente( (Z, W) , duvida(Objeto) )) ), assert(mapa_agente( (Z, W) , duvida(Objeto) )) ) .
											
atualiza_mapa_agente( (X, Y), _) :- 	(Z is X + 1, posicao_pertence_mapa(Z, Y), not(mapa_agente((Z, Y) , _)), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
										Z is X - 1, posicao_pertence_mapa(Z, Y), not(mapa_agente((Z, Y) , _)), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
										Z is Y + 1, posicao_pertence_mapa(X, Z), not(mapa_agente((X, Z) , _)), assert(mapa_agente( (X, Z) , duvida(nada) )) ;
										Z is Y - 1, posicao_pertence_mapa(X, Z), not(mapa_agente((X, Z) , _)), assert(mapa_agente( (X, Z) , duvida(nada) )) ) .
										
conclui_posicao_nada(X, Y) :-	not(mapa_agente((X, Y), _)), assert(mapa_agente( (X, Y) , duvida(nada) )), !.
											
atualiza_duvidas( ) :- 	mapa_agente((X, Y), duvida(Objeto)), Objeto \= nada , findall(_,busca_certeza( (X, Y), Objeto ),_), findall(_,eliminar_duvida( (X, Y), Objeto ),_).


eliminar_duvida((X, Y), Objeto) :-( ( Z1 is Y, W1 is X + 2, mapa_agente((W1, Z1), certeza(Objeto)) );
									( Z2 is Y, W2 is X - 2, mapa_agente((W2, Z2), certeza(Objeto)) );
									( Z3 is Y + 2, W3 is X, mapa_agente((W3, Z3), certeza(Objeto)) );
									( Z4 is Y - 2, W4 is X, mapa_agente((W4, Z4), certeza(Objeto)) );
									( Z5 is Y + 1, W5 is X + 1, mapa_agente((W5, Z5), certeza(Objeto)) );
									( Z6 is Y - 1, W6 is X + 1, mapa_agente((W6, Z6), certeza(Objeto)) );
									( Z7 is Y + 1, W7 is X - 1, mapa_agente((W7, Z7), certeza(Objeto)) );
									( Z8 is Y - 1, W8 is X - 1, mapa_agente((W8, Z8), certeza(Objeto)) ) ),
									mapa_agente((X, Y), duvida(Objeto)), retract(mapa_agente((X, Y), duvida(Objeto))), conclui_posicao_nada(X, Y).


									
busca_certeza((X, Y), Objeto) :-	sensor((Z, W), (X, Y), Objeto), not((sensor((Z, W), (Xi, Yi), Objeto), (Xi \= X ; Yi \= Y))), assert_certeza( (X, Y), Objeto ), remove_sensor_objeto((X, Y), Objeto).			


adjacente_a((X, Y), Objeto) :- 	Z is X + 1, W is Y, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X - 1, W is Y, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X, W is Y + 1, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X, W is Y - 1, mapa_agente((Z, W), certeza(Objeto)) , ! .

transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = buraco, adiciona_a_score(-1000), write('Agente Morreu caindo em um buraco, mas que fim tragico').
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = inimigo(D, _), findall(_,tomar_dano(D),_).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = teleporte, tamanho_mapa(Tx, Ty), Z is (random(Tx - 1) + 1), W is (random(Ty - 1) + 1), retract(posicao(_,_)), assert(posicao(Z, W)), findall(_,transcore_eventos(),_).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), _).

simula_tiro() :- agente_olhando_para(X, Y), mapa_real((X, Y), inimigo(_, _)), dar_dano_inimigo(X, Y).

dar_dano_inimigo(X, Y) :-	mapa_real((X, Y), inimigo(D, V)), Dano is (random(30) + 20), W is V - Dano, retract(mapa_real((X, Y), inimigo(_, _))), ( ( W > 0, assert(mapa_real((X, Y), inimigo(D, W))) ) ; findall(_,assert_certeza( (X, Y), nada),_) ), ! .

agente_olhando_para(X, Y) :-	orientacao(cima), posicao(Z, W), X is Z, Y is W + 1, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(baixo), posicao(Z, W), X is Z, Y is W - 1, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(direita), posicao(Z, W), X is Z + 1, Y is W, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(esquerda), posicao(Z, W), X is Z - 1, Y is W, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
												
adiciona_a_score(X) :- score(Y), W is Y + X, findall(_,retract(score(_)),_), assert(score(W)).

remove_municao() :-	municao(Y), W is Y - 1, retract(municao(_)), assert(municao(W)), !.

recuperar_vida() :- energia(Y), W is Y + 20, (W =< 100; W is 100), retract(energia(_)), assert(energia(W)).

tomar_dano(X) :-	energia(Y), W is Y - X, retract(energia(_)), assert(energia(W)), adiciona_a_score(-X).
tomar_dano(_) :-	energia(W), W =< 0, adiciona_a_score(-1000), write('Agente Morreu para um inimigo, , mas que fim tragico').

adicionar_sensor((X, Y), (Z, W), Objeto) :-	 Objeto \= nada , assert(sensor((X, Y), (Z, W), Objeto)).

remove_sensor((X, Y), Objeto) :-	findall(_,retract(sensor((_,_), (X, Y), Objeto)),_).

distancia_manhatam((X1,Y1), (X2, Y2), C) :-	mod(X1 - X2, X), mod(Y1 - Y2, Y), C is (X + Y).

atualizar_minimo(X) :-	findall(_,retract(minimo(_)),_), assert(minimo(X)).

atualiza_proximo_passo((X, Y), Custo) :- assert(proximo_passo((X,Y), Custo)).

remove_sensor_objeto((X, Y), Objeto) :-	sensor((_,_), (X, Y), Objeto), findall(_, retract(sensor((_,_), (X, Y), Objeto)) ,_), atualiza_duvidas().

mod(X, Y) :- (X < 0, Y is (-X) , !) ; (Y is X , !).
												
posicao_pertence_mapa(X, Y) :- tamanho_mapa(A, B), X < A, X >= 0, Y < B, Y >= 0.									