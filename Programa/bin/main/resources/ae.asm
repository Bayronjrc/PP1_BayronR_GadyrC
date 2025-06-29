# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    resultado_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ae_intermediate.txt
suma:
    # Prólogo simplificado suma
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Guardar parámetros directamente
    sw $a0, a_var
    sw $a1, b_var

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t1 = a * b
    lw $t1, a_var
    lw $t2, b_var
    mul $t0, $t1, $t2
    sw $t0, t1_var

    # RETURN t1
    lw $v0, t1_var
    # Valor de retorno en $v0
    j exit_suma


# Epílogo simplificado suma
exit_suma:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # PARAM 4
    li $a0, 4
    # Parámetro 1 en $a0

    # PARAM 5
    li $a1, 5
    # Parámetro 2 en $a1

    # t2 = CALL suma 2
    jal suma
    sw $v0, t2_var

    # DECLARE resultado INT
    # resultado = t2
    lw $t0, t2_var
    sw $t0, resultado_var

    # WRITE resultado
    lw $a0, resultado_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo simplificado main
exit_main:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

