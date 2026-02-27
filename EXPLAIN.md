Использовались три индекса на статус, автор и created_at, при использовании всех трёх полей postgre будет использовать составной
при поиске по отдельным полям будем использоваться конкретный индекс

Index Scan using idx_documents_author on documents  (cost=0.14..8.16 rows=1 width=330) (actual time=0.016..0.016 rows=0.00 loops=1)
  Index Cond: ((author)::text = 'some_author_name'::text)
  Filter: (status = 'DRAFT'::document_status)
  Index Searches: 1
  Buffers: shared hit=1
Planning Time: 0.087 ms
Execution Time: 0.028 ms
